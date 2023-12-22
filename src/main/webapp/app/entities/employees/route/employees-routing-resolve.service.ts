import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEmployees } from '../employees.model';
import { EmployeesService } from '../service/employees.service';

export const employeesResolve = (route: ActivatedRouteSnapshot): Observable<null | IEmployees> => {
  const id = route.params['id'];
  if (id) {
    return inject(EmployeesService)
      .find(id)
      .pipe(
        mergeMap((employees: HttpResponse<IEmployees>) => {
          if (employees.body) {
            return of(employees.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default employeesResolve;
