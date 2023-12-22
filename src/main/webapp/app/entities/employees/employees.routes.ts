import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { EmployeesComponent } from './list/employees.component';
import { EmployeesDetailComponent } from './detail/employees-detail.component';
import { EmployeesUpdateComponent } from './update/employees-update.component';
import EmployeesResolve from './route/employees-routing-resolve.service';

const employeesRoute: Routes = [
  {
    path: '',
    component: EmployeesComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EmployeesDetailComponent,
    resolve: {
      employees: EmployeesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EmployeesUpdateComponent,
    resolve: {
      employees: EmployeesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EmployeesUpdateComponent,
    resolve: {
      employees: EmployeesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default employeesRoute;
