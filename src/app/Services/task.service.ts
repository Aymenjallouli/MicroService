import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task } from '../models/task.model';
import { Mission } from '../models/mission.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private baseUrl = '/api/tasks'; // Sera redirigé vers le port 8088 via proxy

  constructor(private http: HttpClient) { }

  getAllTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.baseUrl}`);
  }

  getTasksByProject(projectId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.baseUrl}/by-project/${projectId}`);
  }

  getTaskWithDetails(taskId: number): Observable<Task> {
    return this.http.get<Task>(`${this.baseUrl}/${taskId}/with-project-info`);
  }

  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(`${this.baseUrl}`, task);
  }

  updateTask(taskId: number, task: Task): Observable<Task> {
    return this.http.put<Task>(`${this.baseUrl}/${taskId}`, task);
  }

  deleteTask(taskId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${taskId}`);
  }

  uploadTaskPhoto(taskId: number, photo: File): Observable<any> {
    const formData = new FormData();
    formData.append('photo', photo);
    return this.http.put<any>(`${this.baseUrl}/${taskId}`, formData);
  }

  getMissionsByTask(taskId: number): Observable<Mission[]> {
    return this.http.get<Mission[]>(`${this.baseUrl}/missions-by-task/${taskId}`);
  }

  addMissionToTask(taskId: number, mission: Mission): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/add-mission-to-task/${taskId}`, mission);
  }
}
