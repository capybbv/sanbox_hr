import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDepartments } from '../departments.model';
import { DepartmentsService } from '../service/departments.service';

export const departmentsResolve = (route: ActivatedRouteSnapshot): Observable<null | IDepartments> => {
  const id = route.params['id'];
  if (id) {
    return inject(DepartmentsService)
      .find(id)
      .pipe(
        mergeMap((departments: HttpResponse<IDepartments>) => {
          if (departments.body) {
            return of(departments.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default departmentsResolve;
