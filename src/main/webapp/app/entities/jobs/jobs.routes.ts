import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { JobsComponent } from './list/jobs.component';
import { JobsDetailComponent } from './detail/jobs-detail.component';
import { JobsUpdateComponent } from './update/jobs-update.component';
import JobsResolve from './route/jobs-routing-resolve.service';

const jobsRoute: Routes = [
  {
    path: '',
    component: JobsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: JobsDetailComponent,
    resolve: {
      jobs: JobsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: JobsUpdateComponent,
    resolve: {
      jobs: JobsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: JobsUpdateComponent,
    resolve: {
      jobs: JobsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default jobsRoute;
