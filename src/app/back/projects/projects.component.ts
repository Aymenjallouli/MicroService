import { Component, OnInit, AfterViewInit } from '@angular/core';
import { ApiService, Project } from '../../Services/api.service';
import * as L from 'leaflet'; // Import Leaflet

declare var Dropbox: any;

@Component({
  standalone: false,
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css'],
})
export class ProjectsComponent implements OnInit, AfterViewInit {
  projects: Project[] = [];
  selectedProject: Project = this.getEmptyProject();
  selectedFile: File | null = null;
  searchQuery: string = '';
  sortColumn: string = '';
  weatherCity: string = '';

  isAscending: boolean = true;
  currentPage = 0;
  pageSize = 10;
  map: L.Map | undefined;
  weather: any;
  mapExpanded: boolean = false; // Track map size state
  currentTab: string = 'form';


  googleAuthUrl: string | null = null;
  isGoogleAuthenticated: boolean = false;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.fetchProjects();
    this.resetProject();
    this.checkGoogleAuth(); // Check if already authenticated
    
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  initMap(): void {
    // Vérifie si l'onglet de carte est actif avant d'initialiser
    if (this.currentTab !== 'map') {
      return; // Ne pas initialiser la carte si l'onglet n'est pas actif
    }

    // Attendre que le DOM soit prêt
    setTimeout(() => {
      const mapElement = document.getElementById('map');
      
      if (!mapElement) {
        console.error('Map container element not found!');
        return;
      }
      
      // Only initialize if the map isn't already set up
      if (!this.map) {
        try {
          this.map = L.map('map').setView([51.505, -0.09], 13); // Default center: London
          L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
          }).addTo(this.map);

          this.map.on('click', (e: L.LeafletMouseEvent) => {
            const { lat, lng } = e.latlng;
            this.selectedProject.latitude = lat;
            this.selectedProject.longitude = lng;
            console.log('Selected coordinates:', { latitude: lat, longitude: lng });
            L.marker([lat, lng]).addTo(this.map!);
          });
          
          // Update map view if editing a project with coordinates
          if (this.selectedProject.latitude && this.selectedProject.longitude) {
            this.map.setView([this.selectedProject.latitude, this.selectedProject.longitude], 13);
            L.marker([this.selectedProject.latitude, this.selectedProject.longitude]).addTo(this.map);
          }
          
          console.log('Map initialized successfully');
        } catch (e) {
          console.error('Error initializing map:', e);
        }
      } else {
        // Ensure the map renders correctly after tab switch
        this.map.invalidateSize();
        console.log('Map resized');
      }
    }, 100); // petit délai pour s'assurer que le DOM est prêt
  }

  toggleMapSize(): void {
    this.mapExpanded = !this.mapExpanded;
    const mapElement = document.getElementById('map');
    if (mapElement) {
      mapElement.style.height = this.mapExpanded ? '600px' : '400px';
      this.map?.invalidateSize(); // Recalculate map size
    }
  }

  fetchWeather(): void {
    // Use weatherCity if provided, otherwise fallback to selectedProject.location
    const city = this.weatherCity.trim() || this.selectedProject.location;
    if (city) {
      this.apiService.getWeather(city).subscribe(
        (data) => {
          this.weather = data;
          console.log('Weather data:', data);
        },
        (error) => {
          console.error('Error fetching weather:', error);
          this.weather = null;
        }
      );
    } else {
      this.weather = null;
    }
  }
  
// Convert UNIX timestamp to time format
getTime(timestamp: number): string {
  return new Date(timestamp * 1000).toLocaleTimeString();
}


  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      console.log("Selected file:", file);
    }
  }

  openDropboxChooser() {
    const options = {
      success: (files: any) => {
        this.selectedProject.documents = files[0].link;
        alert('File Selected: ' + files[0].link);
      },
      cancel: function () {
        alert('File selection cancelled');
      },
      linkType: 'direct',
      multiselect: false
    };
    Dropbox.choose(options);
  }

  convertDate(date: any): string {
    if (!date) return '';
    if (typeof date === 'string' && date.includes('-')) return date;
    return new Date(date).toISOString().split('T')[0];
  }

  fetchProjects(query: string = '', page: number = 0): void {
    this.apiService.getProjects(query, page).subscribe(
      (response: any) => {
        this.projects = response?.content ?? [];
        this.currentPage = page;
      },
      (error) => console.error("Error loading projects:", error)
    );
  }

  authenticateWithGoogle(): void {
    if (this.googleAuthUrl) {
      window.location.href = this.googleAuthUrl; // Redirect to Google login
    }
  }

  checkGoogleAuth(): void {
    // For simplicity, assume authenticated if redirected back with a success param
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('auth') === 'success') {
      this.isGoogleAuthenticated = true;
      window.history.replaceState({}, document.title, window.location.pathname); // Clean URL
    }
  }

  saveProject(): void {
    if (!this.selectedProject) {
      console.error('Project data is missing');
      return;
    }

    // If a file is selected, handle file upload separately (optional)
    if (this.selectedFile) {
      console.warn("File upload not implemented in this example. Use FormData if needed.");
    }

    if (this.selectedProject.id) {
      this.apiService.updateProject(this.selectedProject.id, this.selectedProject).subscribe(
        (updatedProject) => {
          console.log("Project updated:", updatedProject);
          this.projects = this.projects.map((project) =>
            project.id === updatedProject.id ? updatedProject : project
          );
          this.resetProject();
          this.initMap(); // Re-initialize map after reset
        },
        (error) => console.error("Error updating project:", error)
      );
    } else {
      this.apiService.createProject(this.selectedProject).subscribe(
        (newProject) => {
          console.log("Project added:", newProject);
          this.projects.push(newProject);
          this.resetProject();
          this.initMap(); // Re-initialize map after reset
        },
        (error) => console.error("Error adding project:", error)
      );
    }
  }

  editProject(project: Project): void {
    this.selectedProject = {
      ...project,
      startDate: this.convertDate(project.startDate),
      endDate: this.convertDate(project.endDate)
    };
    // Update map with existing coordinates
    if (this.map && this.selectedProject.latitude && this.selectedProject.longitude) {
      this.map.setView([this.selectedProject.latitude, this.selectedProject.longitude], 13);
      L.marker([this.selectedProject.latitude, this.selectedProject.longitude]).addTo(this.map);
    }
  }

  deleteProject(id: number): void {
    this.apiService.deleteProject(id).subscribe(() => {
      this.projects = this.projects.filter((p) => p.id !== id);
    });
  }

  resetProject(): void {
    this.selectedProject = this.getEmptyProject();
    this.selectedFile = null;
  }

  getEmptyProject(): Project {
    return {
      id: undefined,
      name: '',
      description: '',
      status: '',
      startDate: '',
      endDate: '',
      location: '',
      documents: '',
      images: '',
      latitude: undefined,
      longitude: undefined
    };
  }

  downloadFile(fileName: string): void {
    if (!fileName) {
      console.error("No file to download");
      return;
    }

    this.apiService.downloadEtudeFile(fileName).subscribe(
      (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
      },
      (error) => console.error("Error downloading file:", error)
    );
  }

  onSearchInput(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.searchQuery = inputElement?.value || '';
    this.fetchProjects(this.searchQuery);
  }

  sortProjects(column: string) {
    const isAscending = this.sortColumn === column ? !this.isAscending : true;
    this.sortColumn = column;
    this.isAscending = isAscending;

    this.projects.sort((a: any, b: any) => {
      if (a[column] < b[column]) return this.isAscending ? -1 : 1;
      if (a[column] > b[column]) return this.isAscending ? 1 : -1;
      return 0;
    });
  }
}