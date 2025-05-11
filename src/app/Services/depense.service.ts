import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Depense } from '../models/depense.model';

@Injectable({
  providedIn: 'root'
})
export class DepenseService {
  private baseUrl = '/api/depenses';

  constructor(private http: HttpClient) { }

  // Récupérer toutes les dépenses d'un projet
  getDepensesByProject(projectId: number): Observable<Depense[]> {
    return this.http.get<Depense[]>(`${this.baseUrl}/findByProject/${projectId}`);
  }

  // Récupérer une dépense par ID
  getDepenseById(id: number): Observable<Depense> {
    return this.http.get<Depense>(`${this.baseUrl}/${id}`);
  }

  // Créer une nouvelle dépense
  createDepense(depense: Depense): Observable<Depense> {
    return this.http.post<Depense>(`${this.baseUrl}/add`, depense);
  }

  // Mettre à jour une dépense
  updateDepense(id: number, depense: Depense): Observable<Depense> {
    return this.http.put<Depense>(`${this.baseUrl}/update/${id}`, depense);
  }

  // Supprimer une dépense
  deleteDepense(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // Uploader un fichier pour une dépense
  uploadFile(id: number, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.baseUrl}/upload/${id}`, formData, { responseType: 'text' });
  }
  // Récupérer tous les projets
  getAllProjects(): Observable<any[]> {
    // Essayer avec l'endpoint 'projects' au lieu de 'allProject'
    return this.http.get<any[]>(`${this.baseUrl}/projects`);
  }

  // Vérifier si un fichier est attaché à une dépense
  checkFile(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/check-file/${id}`);
  }

  // Télécharger le fichier attaché à une dépense
  downloadFile(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/getfile/${id}`, { responseType: 'blob' });
  }
}
