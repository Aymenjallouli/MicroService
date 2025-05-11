import { APP_INITIALIZER, NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ToastrModule } from 'ngx-toastr';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { CorsInterceptor } from './interceptors/cors.interceptor';

import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';

import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

import { BackHomeComponent } from './back/back-home/back-home.component';
import { ReclamationComponent } from './components/Reclamationn/reclamation/reclamation.component';
import { ReponseComponent } from './components/Reponsee/reponse/reponse.component';
import { ReclamationListComponent } from './components/Reclamationn/reclamation-list/reclamation-list.component';
import { ReclamationEditComponent } from './components/Reclamationn/reclamation-edit/reclamation-edit.component';
import { ReponseListComponent } from './components/Reponsee/reponse-list/reponse-list.component';
import { UserManagementComponentComponent } from './back/UserComponents/user-management-component/user-management-component.component';
import { ProfileBackComponent } from './back/UserComponents/profile-back/profile-back.component';
import { UpdateUserComponent } from './back/UserComponents/update-user/update-user.component';
import { LoginComponent } from './login/login.component';
import { NavBarComponent } from './back/nav-bar/nav-bar.component';

  
import { NavBarFrontComponent } from './front/nav-bar-front/nav-bar-front.component';
import { ReclamationStatistiqueComponent } from './components/Reclamationn/reclamation-statistique/reclamation-statistique.component';
import { ReclamationEditUserComponent } from './components/Reclamationn/reclamation-edit-user/reclamation-edit-user.component';
import { ReclamationListUserComponent } from './components/Reclamationn/reclamation-list-user/reclamation-list-user.component';
import { AllFilesComponent } from './back/all-files/all-files.component';
import { ReponseEditComponent } from './components/Reponsee/reponse-edit/reponse-edit.component';

import { SidenavComponent } from './back/sidenav/sidenav.component';
import { BackComponent } from './back/back.component';
import { FrontComponent } from './front/front.component';
import { UserServiceService } from './Services/UserService/user-service.service';
import { ProfileComponent } from './profile/profile.component';
import { UpdateProfileComponent } from './updateprofile/updateprofile.component';




import { ArticleCardComponent } from './front/article-cards/article-cards.component';


import { ArticlesComponent } from './front/articles/articles.component';
import { OrderSummaryComponent } from './front/order-summary/order-summary.component';
import { BasketComponent } from './back/basket/basket.component';
import { AddArticleComponent } from './back/article/add-article/add-article.component';
import { ModifyArticleComponent } from './back/article/modify-article/modify-article.component';
import { AddBasketComponent } from './back/basket/add-basket/add-basket.component';
import { ModifyBasketComponent } from './back/basket/modify-basket/modify-basket.component';
import { OrderComponent } from './back/order/order.component';
import { AddOrderComponent } from './back/order/add-order/add-order.component';
function initializeKeycloak(keycloak: KeycloakService) {

 
 
 
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8080',
        realm: 'constructionRealm',
        clientId: 'frontapp',
      },
      initOptions: {
        onLoad: 'check-sso',
        checkLoginIframe: false,
      },
      bearerExcludedUrls: ['/assets'],
    });
}
import { ArticleComponent } from './back/article/article.component';
import { EtudesComponent } from './back/etudes/etudes.component';
import { MapComponent } from './back/map/map.component';
import { ProjectsComponent } from './back/projects/projects.component';
import { ChatbotComponent } from './components/chatbot/chatbot.component';
import { HomeComponent } from './front/home/home.component';
import { ProjectFrontListComponent } from './front/project-front-list/project-front-list.component';
import { WeatherFrontComponent } from './front/weather-front/weather-front.component';
import { EtudesFrontComponent } from './front/etudes-front/etudes-front.component';
import { PredictionComponent } from './prediction/prediction.component';
import { BuildingCostComponent } from './building-cost/building-cost.component';

@NgModule({
  declarations: [
    AppComponent,
    BackHomeComponent,
    LoginComponent,
    NavBarComponent,
    NavBarFrontComponent,
    ReclamationComponent,
    ReponseComponent,
    ReclamationListComponent,
    ReclamationEditComponent,
    ReponseListComponent,
    UserManagementComponentComponent,
    ProfileBackComponent,
    UpdateUserComponent,
    ReclamationStatistiqueComponent,
    ReclamationEditUserComponent,
    ReclamationListUserComponent,
    AllFilesComponent,
    ReponseEditComponent,
    UpdateProfileComponent,
    HomeComponent,
    SidenavComponent,
    BackComponent,
    FrontComponent,
    ProfileComponent,
    ArticleCardComponent,
    ArticleComponent,
    ArticlesComponent,
    OrderSummaryComponent,
    BasketComponent,
    AddArticleComponent,
    ModifyArticleComponent,
    AddBasketComponent,
    ModifyBasketComponent,
    OrderComponent,
    AddOrderComponent,
    EtudesComponent,
    MapComponent,
    ProjectsComponent,
    ChatbotComponent,
    ProjectFrontListComponent,
    WeatherFrontComponent,
    EtudesFrontComponent,    PredictionComponent,
    BuildingCostComponent,
  ],  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
    NgxChartsModule,
    KeycloakAngularModule,
    ToastrModule.forRoot({
      timeOut: 3000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
      progressBar: true,
    }),
    MatIconModule,
    MatSelectModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    MatTableModule,
    MatToolbarModule,
    MatButtonModule,
  ],  providers: [
    {
      provide: APP_INITIALIZER,

      useFactory: (keycloak: KeycloakService) => () =>
        keycloak.init({
          config: {
            url: 'http://localhost:8080',
            realm: 'constructionRealm',
            clientId: 'frontapp',
          },
          initOptions: {
            onLoad: 'check-sso',
            checkLoginIframe: false,
          },
          bearerExcludedUrls: ['/assets'],
        }),
      multi: true,
      deps: [KeycloakService],
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: CorsInterceptor,
      multi: true
    },
    provideAnimationsAsync(),
    UserServiceService,
  ],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AppModule {}
