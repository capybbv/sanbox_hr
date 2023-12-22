import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { CountriesComponent } from './list/countries.component';
import { CountriesDetailComponent } from './detail/countries-detail.component';
import { CountriesUpdateComponent } from './update/countries-update.component';
import CountriesResolve from './route/countries-routing-resolve.service';

const countriesRoute: Routes = [
  {
    path: '',
    component: CountriesComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CountriesDetailComponent,
    resolve: {
      countries: CountriesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CountriesUpdateComponent,
    resolve: {
      countries: CountriesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CountriesUpdateComponent,
    resolve: {
      countries: CountriesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default countriesRoute;
