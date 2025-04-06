import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TaskService } from '../../../services/task.service';
import { Task } from '../../../models/task.model';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css'],
  standalone: true,
  imports: [CommonModule, RouterLink]
})
export class TaskListComponent implements OnInit {
  tasks: Task[] = [];
  loading = false;
  errorMessage = '';
  filterStatus: string = '';

  constructor(
    private taskService: TaskService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(status: string = ''): void {
    this.loading = true;
    this.filterStatus = status;
    
    this.taskService.getTasks(status)
      .subscribe({
        next: (tasks: Task[]) => {
          this.tasks = tasks;
          this.loading = false;
        },
        error: (error: any) => {
          this.errorMessage = 'Failed to load tasks. Please try again.';
          this.loading = false;
          
          // Handle unauthorized error - redirect to login
          if (error.status === 401) {
            this.authService.logout();
            this.router.navigate(['/login']);
          }
        }
      });
  }

  getPriorityClass(priority: string): string {
    switch (priority) {
      case 'HIGH': return 'priority-high';
      case 'MEDIUM': return 'priority-medium';
      case 'LOW': return 'priority-low';
      default: return '';
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'TODO': return 'status-todo';
      case 'IN_PROGRESS': return 'status-in-progress';
      case 'DONE': return 'status-done';
      default: return '';
    }
  }

  deleteTask(id: number): void {
    if (confirm('Are you sure you want to delete this task?')) {
      this.taskService.deleteTask(id)
        .subscribe({
          next: () => {
            this.tasks = this.tasks.filter(task => task.id !== id);
          },
          error: (error: any) => {
            this.errorMessage = 'Failed to delete task. Please try again.';
            
            // Handle unauthorized error - redirect to login
            if (error.status === 401) {
              this.authService.logout();
              this.router.navigate(['/login']);
            }
          }
        });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
