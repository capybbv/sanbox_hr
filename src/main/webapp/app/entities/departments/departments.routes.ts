import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { DepartmentsComponent } from './list/departments.component';
import { DepartmentsDetailComponent } from './detail/departments-detail.component';
import { DepartmentsUpdateComponent } from './update/departments-update.component';
import DepartmentsResolve from './route/departments-routing-resolve.service';

const departmentsRoute: Routes = [
  {
    path: '',
    component: DepartmentsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DepartmentsDetailComponent,
    resolve: {
      departments: DepartmentsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DepartmentsUpdateComponent,
    resolve: {
      departments: DepartmentsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DepartmentsUpdateComponent,
    resolve: {
      departments: DepartmentsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default departmentsRoute;
