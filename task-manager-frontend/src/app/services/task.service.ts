import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task } from '../models/task.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) { }

  // Get all tasks with optional status filter
  getTasks(status?: string): Observable<Task[]> {
    if (status && status.trim() !== '') {
      return this.http.get<Task[]>(`${this.apiUrl}/status/${status}`);
    } else {
      return this.http.get<Task[]>(this.apiUrl);
    }
  }

  // Get a single task by ID
  getTask(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  // Original method - can be used as an alias
  getAllTasks(): Observable<Task[]> {
    return this.getTasks();
  }

  // Original method - can be used as an alias
  getTaskById(id: number): Observable<Task> {
    return this.getTask(id);
  }

  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }

  updateTask(id: number, task: Task): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getTasksByStatus(status: string): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/status/${status}`);
  }

  getTasksByPriority(priority: string): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/priority/${priority}`);
  }
}
