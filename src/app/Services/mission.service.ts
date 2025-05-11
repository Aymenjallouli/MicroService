import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mission } from '../models/mission.model';
import { Task } from '../models/task.model';

@Injectable({
  providedIn: 'root'
})
export class MissionService {
  private baseUrl = '/api/mission'; // Sera redirigé vers le port 8088 via proxy

  constructor(private http: HttpClient) { }

  getAllMissions(): Observable<Mission[]> {
    return this.http.get<Mission[]>(`${this.baseUrl}`);
  }

  getMissionById(id: number): Observable<Mission> {
    return this.http.get<Mission>(`${this.baseUrl}/${id}`);
  }

  createMission(mission: Mission): Observable<Mission> {
    return this.http.post<Mission>(`${this.baseUrl}`, mission);
  }

  createMissionForTask(taskId: number, mission: Mission): Observable<Mission> {
    return this.http.post<Mission>(`${this.baseUrl}/assign-to-task/${taskId}`, mission);
  }

  deleteMission(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getTasksByMission(missionId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.baseUrl}/${missionId}/tasks`);
  }
}
