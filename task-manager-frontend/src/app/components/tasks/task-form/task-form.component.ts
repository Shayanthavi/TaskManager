import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TaskService } from '../../../services/task.service';
import { Task } from '../../../models/task.model';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-task-form',
  templateUrl: './task-form.component.html',
  styleUrls: ['./task-form.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink]
})
export class TaskFormComponent implements OnInit {
  taskForm!: FormGroup;
  loading = false;
  submitted = false;
  errorMessage = '';
  isEditMode = false;
  taskId: number | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private taskService: TaskService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.initForm();
    
    // Check if we're in edit mode
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.isEditMode = true;
        this.taskId = +id;
        this.loadTask(this.taskId);
      }
    });
  }

  initForm(): void {
    this.taskForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required]],
      dueDate: ['', [Validators.required]],
      priority: ['MEDIUM', [Validators.required]],
      status: ['TODO', [Validators.required]]
    });
  }

  loadTask(id: number): void {
    this.loading = true;
    this.taskService.getTask(id).subscribe({
      next: (task: Task) => {
        // Format the date to be compatible with input type="date"
        const dueDate = new Date(task.dueDate);
        const formattedDate = dueDate.toISOString().split('T')[0];
        
        this.taskForm.patchValue({
          title: task.title,
          description: task.description,
          dueDate: formattedDate,
          priority: task.priority,
          status: task.status
        });
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = 'Failed to load task. Please try again.';
        this.loading = false;
        
        // Handle unauthorized error - redirect to login
        if (error.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login']);
        }
      }
    });
  }

  onSubmit(): void {
    this.submitted = true;
    
    if (this.taskForm.invalid) {
      return;
    }
    
    this.loading = true;
    
    const task: Task = this.taskForm.value;
    
    if (this.isEditMode && this.taskId) {
      // Update existing task
      task.id = this.taskId;
      this.taskService.updateTask(this.taskId, task).subscribe({
        next: () => {
          this.router.navigate(['/tasks']);
        },
        error: (error: any) => {
          this.errorMessage = 'Failed to update task. Please try again.';
          this.loading = false;
          
          // Handle unauthorized error - redirect to login
          if (error.status === 401) {
            this.authService.logout();
            this.router.navigate(['/login']);
          }
        }
      });
    } else {
      // Create new task
      this.taskService.createTask(task).subscribe({
        next: () => {
          this.router.navigate(['/tasks']);
        },
        error: (error: any) => {
          this.errorMessage = 'Failed to create task. Please try again.';
          this.loading = false;
          
          // Handle unauthorized error - redirect to login
          if (error.status === 401) {
            this.authService.logout();
            this.router.navigate(['/login']);
          }
        }
      });
    }
  }
  
  cancel(): void {
    this.router.navigate(['/tasks']);
  }
}
