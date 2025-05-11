import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Task } from '../../../models/task.model';
import { Mission } from '../../../models/mission.model';
import { TaskService } from '../../../Services/task.service';
import { MissionService } from '../../../Services/mission.service';

@Component({
  selector: 'app-task-details',
  templateUrl: './task-details.component.html',
  styleUrls: ['./task-details.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule]
})
export class TaskDetailsComponent implements OnInit {
  taskId: number = 0;
  projectId: number = 0;
  task: Task | null = null;
  missions: Mission[] = [];
  isLoading = false;
  
  newMission: Mission = {
    title: '',
    description: '',
    dueDate: new Date().toISOString().split('T')[0],
    status: 'NEW'
  };

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private missionService: MissionService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.taskId = +params['taskId'];
      this.projectId = +params['id'];
      this.loadTask();
      this.loadMissions();
    });
  }

  loadTask() {
    this.isLoading = true;
    this.taskService.getTaskWithDetails(this.taskId).subscribe({
      next: (data) => {
        this.task = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading task details', error);
        this.isLoading = false;
        this.toastr.warning('Impossible de charger les détails de la tâche');
      }
    });
  }

  loadMissions() {
    this.isLoading = true;
    this.taskService.getMissionsByTask(this.taskId).subscribe({
      next: (data) => {
        this.missions = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading missions', error);
        this.isLoading = false;
        this.toastr.warning('Impossible de charger les missions');
      }
    });
  }

  saveMission() {
    if (!this.newMission.title || !this.newMission.dueDate) {
      this.toastr.warning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    this.isLoading = true;
    this.taskService.addMissionToTask(this.taskId, this.newMission).subscribe({
      next: () => {
        this.toastr.success('Mission ajoutée avec succès');
        this.newMission = {
          title: '',
          description: '',
          dueDate: new Date().toISOString().split('T')[0],
          status: 'NEW'
        };
        this.loadMissions();
      },
      error: (error) => {
        console.error('Error adding mission', error);
        this.isLoading = false;
        this.toastr.error('Erreur lors de l\'ajout de la mission');
      }
    });
  }

  updateTask() {
    if (!this.task) return;
    
    this.isLoading = true;
    this.taskService.updateTask(this.taskId, this.task).subscribe({
      next: () => {
        this.toastr.success('Tâche mise à jour avec succès');
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error updating task', error);
        this.isLoading = false;
        this.toastr.error('Erreur lors de la mise à jour de la tâche');
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'UNREACHED': return 'bg-secondary';
      case 'INPROGRESS': return 'bg-warning';
      case 'DONE': return 'bg-success';
      case 'OVERDUE': return 'bg-danger';
      default: return 'bg-light';
    }
  }

  getMissionStatusClass(status: string): string {
    switch (status) {
      case 'NEW': return 'bg-info';
      case 'IN_PROGRESS': return 'bg-warning';
      case 'COMPLETED': return 'bg-success';
      case 'BLOCKED': return 'bg-danger';
      default: return 'bg-light';
    }
  }
}
