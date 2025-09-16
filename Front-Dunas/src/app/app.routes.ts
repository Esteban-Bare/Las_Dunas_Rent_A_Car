import { Routes } from '@angular/router';
import {HomeComponent} from './home/home.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {BackofficeIndexComponent} from './backoffice/backoffice-index/backoffice-index.component';
import {LoginComponent} from './login/login.component';
import {UnauthorizedComponent} from './unauthorized/unauthorized.component';
import {AuthGuard} from './guard/auth.guard';
import {LoggedGuard} from './guard/logged.guard';
import {RegisterComponent} from './register/register.component';
import {ReservationComponent} from './reservation/reservation.component';
import {CreateReservationComponent} from './create-reservation/create-reservation.component';
import {ClientReservationsComponent} from './client-reservations/client-reservations.component';
import {ClientPaymentsComponent} from './client-payments/client-payments.component';
import {VehiclesComponent} from './vehicles/vehicles.component';
import {VehicleDetailsComponent} from './vehicle-details/vehicle-details.component';
import {StoresComponent} from './stores/stores.component';
import {BackofficeUsersComponent} from './backoffice/backoffice-users/backoffice-users.component';
import {BackofficeVehiclesComponent} from './backoffice/backoffice-vehicles/backoffice-vehicles.component';
import {BackofficeReservationsComponent} from './backoffice/backoffice-reservations/backoffice-reservations.component';
import {ClientRentalsComponent} from './client-rentals/client-rentals.component';
import {BackofficeRentalsComponent} from './backoffice/backoffice-rentals/backoffice-rentals.component';
import {BackofficePaymentsComponent} from './backoffice/backoffice-payments/backoffice-payments.component';

export const routes: Routes = [
  {path: "", component: HomeComponent},
  {path: "dashboard", component: DashboardComponent, canActivate: [AuthGuard]},
  {path: "backoffice", component:BackofficeIndexComponent,canActivate: [AuthGuard], data : { role: 'manager', resource: 'manager-backoffice' } },
  {path: "admin/backoffice/users", component:BackofficeUsersComponent,canActivate: [AuthGuard], data : { role: 'admin', resource: 'admin-backoffice' } },
  {path: "backoffice/vehicles", component:BackofficeVehiclesComponent,canActivate: [AuthGuard], data : { role: 'manager', resource: 'manager-backoffice' } },
  {path: "backoffice/reservations", component:BackofficeReservationsComponent,canActivate: [AuthGuard], data : { role: 'manager', resource: 'manager-backoffice' } },
  {path: "backoffice/rentals", component:BackofficeRentalsComponent,canActivate: [AuthGuard], data : { role: 'manager', resource: 'manager-backoffice' } },
  {path: "backoffice/payments", component:BackofficePaymentsComponent,canActivate: [AuthGuard], data : { role: 'manager', resource: 'manager-backoffice' } },
  {path: "login", component: LoginComponent, canActivate: [LoggedGuard]},
  {path: "admin/backoffice", component: BackofficeIndexComponent,canActivate: [AuthGuard], data : { role: 'admin', resource: 'admin-backoffice' } },
  {path: "unauthorized", component: UnauthorizedComponent},
  {path: "register", component: RegisterComponent, canActivate: [LoggedGuard]},
  {path: "reservations", component: ReservationComponent},
  {path: "create-reservation", component: CreateReservationComponent},
  {path: "client-reservations", component: ClientReservationsComponent, canActivate: [AuthGuard]},
  {path: "client-rentals", component: ClientRentalsComponent, canActivate: [AuthGuard]},
  {path: "client-payments", component: ClientPaymentsComponent, canActivate: [AuthGuard]},
  {path: "vehicles", component: VehiclesComponent},
  {path: "vehicle/:id", component: VehicleDetailsComponent},
  {path: "stores", component: StoresComponent}
];
