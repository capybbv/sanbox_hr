import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { LocationsComponent } from './list/locations.component';
import { LocationsDetailComponent } from './detail/locations-detail.component';
import { LocationsUpdateComponent } from './update/locations-update.component';
import LocationsResolve from './route/locations-routing-resolve.service';

const locationsRoute: Routes = [
  {
    path: '',
    component: LocationsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LocationsDetailComponent,
    resolve: {
      locations: LocationsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LocationsUpdateComponent,
    resolve: {
      locations: LocationsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LocationsUpdateComponent,
    resolve: {
      locations: LocationsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default locationsRoute;
