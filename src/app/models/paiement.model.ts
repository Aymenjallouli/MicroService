import { Facture } from './facture.model';

export interface Paiement {
  id?: number;
  montant: number;
  datePaiement: string; // Changed from Date to string
  methodePaiement: string; // Changed from specific types to string
  reference: string;
  status: string;
  facture: Facture | null;
}
