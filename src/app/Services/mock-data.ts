// Mock data pour le développement frontend
import { Depense } from '../models/depense.model';
import { Facture } from '../models/facture.model';
import { Paiement } from '../models/paiement.model';

export const MOCK_PROJECTS = [
  { id: 1, nomProjet: 'Projet Construction Résidentielle', description: 'Construction d\'un immeuble résidentiel' },
  { id: 2, nomProjet: 'Projet Rénovation Centre Commercial', description: 'Rénovation du centre commercial' },
  { id: 3, nomProjet: 'Projet Infrastructure Routière', description: 'Construction d\'une nouvelle route nationale' }
];

export const MOCK_DEPENSES: Depense[] = [
  { 
    id: 1, 
    description: 'Achat de matériaux de construction',
    montant: 15000,
    date: new Date('2025-03-15'),
    type: 'MATERIEL',
    statut: 'VALIDEE',
    idProjet: 1,
    beneficiaire: 'Fournisseur Matériaux SA'
  },
  { 
    id: 2, 
    description: 'Main d\'oeuvre - semaine 12',
    montant: 8500,
    date: new Date('2025-03-20'),
    type: 'MAIN_DOEUVRE',
    statut: 'VALIDEE',
    idProjet: 1,
    beneficiaire: 'Équipe de construction'
  },
  { 
    id: 3, 
    description: 'Location d\'équipement lourd',
    montant: 3200,
    date: new Date('2025-03-25'),
    type: 'AUTRES',
    statut: 'EN_ATTENTE',
    idProjet: 1,
    beneficiaire: 'Location Équipement Pro'
  }
];

export const MOCK_FACTURES: Facture[] = [
  {
    id: 1,
    numero: 'FACT-2025-001',
    dateFacture: new Date('2025-03-16'),
    montant: 15000,
    statut: 'PAYEE',
    description: 'Facture pour achat de matériaux',
    projectId: 1,
    projectName: 'Projet Construction Résidentielle'
  },
  {
    id: 2,
    numero: 'FACT-2025-002',
    dateFacture: new Date('2025-03-21'),
    montant: 8500,
    statut: 'EMISE',
    description: 'Facture pour main d\'oeuvre',
    projectId: 1,
    projectName: 'Projet Construction Résidentielle'
  }
];

export const MOCK_PAIEMENTS: Paiement[] = [
  {
    id: 1,
    datePaiement: new Date('2025-03-18'),
    montant: 15000,
    methodePaiement: 'VIREMENT_BANCAIRE',
    reference: 'VIR-20250318-001',
    facture: { id: 1 },
    status: 'COMPLETE'
  }
];

// Fonction utilitaire pour générer un ID unique
export function generateId(items: any[]): number {
  return items.reduce((max, item) => Math.max(max, item.id || 0), 0) + 1;
}
