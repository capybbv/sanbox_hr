import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IJobs } from '../jobs.model';
import { JobsService } from '../service/jobs.service';

export const jobsResolve = (route: ActivatedRouteSnapshot): Observable<null | IJobs> => {
  const id = route.params['id'];
  if (id) {
    return inject(JobsService)
      .find(id)
      .pipe(
        mergeMap((jobs: HttpResponse<IJobs>) => {
          if (jobs.body) {
            return of(jobs.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default jobsResolve;
