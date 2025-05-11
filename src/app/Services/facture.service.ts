import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Facture } from '../models/facture.model';

@Injectable({
  providedIn: 'root'
})
export class FactureService {
  private baseUrl = '/api/factures';

  constructor(private http: HttpClient) { }

  // Récupérer toutes les factures
  getAllFactures(): Observable<Facture[]> {
    return this.http.get<Facture[]>(this.baseUrl);
  }  // Récupérer les factures avec les données des projets
  getAllFacturesWithProjects(): Observable<Facture[]> {
    // Utiliser la bonne URL pour récupérer les factures avec les détails de projet
    return this.http.get<Facture[]>(`${this.baseUrl}/with-projects`);
  }
  
  // Récupérer les factures d'un projet spécifique
  getFacturesByProjectId(projectId: number): Observable<Facture[]> {
    // Récupérer toutes les factures et filtrer côté client
    return new Observable<Facture[]>(observer => {
      this.getAllFacturesWithProjects().subscribe({
        next: (factures) => {
          const filteredFactures = factures.filter(f => f.projectId === projectId);
          observer.next(filteredFactures);
          observer.complete();
        },
        error: (error) => observer.error(error)
      });
    });
  }

  // Récupérer une facture par ID
  getFactureById(id: number): Observable<Facture> {
    return this.http.get<Facture>(`${this.baseUrl}/${id}`);
  }

  // Créer une nouvelle facture
  createFacture(facture: Facture): Observable<Facture> {
    return this.http.post<Facture>(this.baseUrl, facture);
  }

  // Créer une facture associée à un projet
  createFactureForProject(projectId: number, facture: Facture): Observable<Facture> {
    return this.http.post<Facture>(`${this.baseUrl}/project/${projectId}`, facture);
  }

  // Mettre à jour une facture
  updateFacture(id: number, facture: Facture): Observable<Facture> {
    return this.http.put<Facture>(`${this.baseUrl}/${id}`, facture);
  }

  // Supprimer une facture
  deleteFacture(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
  // Générer une facture à partir d'une dépense
  generateFromDepense(depenseId: number): Observable<Facture> {
    console.log(`Generating invoice from expense ID: ${depenseId}`);
    return this.http.post<Facture>(`${this.baseUrl}/generate-from-depense/${depenseId}`, {});
  }

  // Télécharger un modèle CSV
  downloadTemplate(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/template`, { responseType: 'blob' });
  }

  // Uploader un fichier CSV de factures
  uploadInvoiceFile(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(`${this.baseUrl}/upload-file`, formData);
  }
}
