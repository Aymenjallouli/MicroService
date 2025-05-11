export interface Mission {
  id?: number;
  title: string;
  description?: string;
  dueDate: string;
  assigneeId?: number;
  status: string;
  taskIds?: number[];
}
