import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IEmployees, NewEmployees } from '../employees.model';

export type PartialUpdateEmployees = Partial<IEmployees> & Pick<IEmployees, 'id'>;

type RestOf<T extends IEmployees | NewEmployees> = Omit<T, 'hireDate'> & {
  hireDate?: string | null;
};

export type RestEmployees = RestOf<IEmployees>;

export type NewRestEmployees = RestOf<NewEmployees>;

export type PartialUpdateRestEmployees = RestOf<PartialUpdateEmployees>;

export type EntityResponseType = HttpResponse<IEmployees>;
export type EntityArrayResponseType = HttpResponse<IEmployees[]>;

@Injectable({ providedIn: 'root' })
export class EmployeesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/employees');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/employees/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(employees: NewEmployees): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(employees);
    return this.http
      .post<RestEmployees>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(employees: IEmployees): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(employees);
    return this.http
      .put<RestEmployees>(`${this.resourceUrl}/${this.getEmployeesIdentifier(employees)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(employees: PartialUpdateEmployees): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(employees);
    return this.http
      .patch<RestEmployees>(`${this.resourceUrl}/${this.getEmployeesIdentifier(employees)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestEmployees>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEmployees[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestEmployees[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<IEmployees[]>()], asapScheduler)),
    );
  }

  getEmployeesIdentifier(employees: Pick<IEmployees, 'id'>): number {
    return employees.id;
  }

  compareEmployees(o1: Pick<IEmployees, 'id'> | null, o2: Pick<IEmployees, 'id'> | null): boolean {
    return o1 && o2 ? this.getEmployeesIdentifier(o1) === this.getEmployeesIdentifier(o2) : o1 === o2;
  }

  addEmployeesToCollectionIfMissing<Type extends Pick<IEmployees, 'id'>>(
    employeesCollection: Type[],
    ...employeesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const employees: Type[] = employeesToCheck.filter(isPresent);
    if (employees.length > 0) {
      const employeesCollectionIdentifiers = employeesCollection.map(employeesItem => this.getEmployeesIdentifier(employeesItem)!);
      const employeesToAdd = employees.filter(employeesItem => {
        const employeesIdentifier = this.getEmployeesIdentifier(employeesItem);
        if (employeesCollectionIdentifiers.includes(employeesIdentifier)) {
          return false;
        }
        employeesCollectionIdentifiers.push(employeesIdentifier);
        return true;
      });
      return [...employeesToAdd, ...employeesCollection];
    }
    return employeesCollection;
  }

  protected convertDateFromClient<T extends IEmployees | NewEmployees | PartialUpdateEmployees>(employees: T): RestOf<T> {
    return {
      ...employees,
      hireDate: employees.hireDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restEmployees: RestEmployees): IEmployees {
    return {
      ...restEmployees,
      hireDate: restEmployees.hireDate ? dayjs(restEmployees.hireDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestEmployees>): HttpResponse<IEmployees> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestEmployees[]>): HttpResponse<IEmployees[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
