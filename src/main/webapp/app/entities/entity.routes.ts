import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'region',
    data: { pageTitle: 'sanbox2App.region.home.title' },
    loadChildren: () => import('./region/region.routes'),
  },
  {
    path: 'countries',
    data: { pageTitle: 'sanbox2App.countries.home.title' },
    loadChildren: () => import('./countries/countries.routes'),
  },
  {
    path: 'locations',
    data: { pageTitle: 'sanbox2App.locations.home.title' },
    loadChildren: () => import('./locations/locations.routes'),
  },
  {
    path: 'jobs',
    data: { pageTitle: 'sanbox2App.jobs.home.title' },
    loadChildren: () => import('./jobs/jobs.routes'),
  },
  {
    path: 'departments',
    data: { pageTitle: 'sanbox2App.departments.home.title' },
    loadChildren: () => import('./departments/departments.routes'),
  },
  {
    path: 'employees',
    data: { pageTitle: 'sanbox2App.employees.home.title' },
    loadChildren: () => import('./employees/employees.routes'),
  },
  {
    path: 'job-history',
    data: { pageTitle: 'sanbox2App.jobHistory.home.title' },
    loadChildren: () => import('./job-history/job-history.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
