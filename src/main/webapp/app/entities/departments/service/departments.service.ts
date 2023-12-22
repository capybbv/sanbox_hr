import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IDepartments, NewDepartments } from '../departments.model';

export type PartialUpdateDepartments = Partial<IDepartments> & Pick<IDepartments, 'id'>;

export type EntityResponseType = HttpResponse<IDepartments>;
export type EntityArrayResponseType = HttpResponse<IDepartments[]>;

@Injectable({ providedIn: 'root' })
export class DepartmentsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/departments');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/departments/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(departments: NewDepartments): Observable<EntityResponseType> {
    return this.http.post<IDepartments>(this.resourceUrl, departments, { observe: 'response' });
  }

  update(departments: IDepartments): Observable<EntityResponseType> {
    return this.http.put<IDepartments>(`${this.resourceUrl}/${this.getDepartmentsIdentifier(departments)}`, departments, {
      observe: 'response',
    });
  }

  partialUpdate(departments: PartialUpdateDepartments): Observable<EntityResponseType> {
    return this.http.patch<IDepartments>(`${this.resourceUrl}/${this.getDepartmentsIdentifier(departments)}`, departments, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDepartments>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDepartments[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IDepartments[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IDepartments[]>()], asapScheduler)));
  }

  getDepartmentsIdentifier(departments: Pick<IDepartments, 'id'>): number {
    return departments.id;
  }

  compareDepartments(o1: Pick<IDepartments, 'id'> | null, o2: Pick<IDepartments, 'id'> | null): boolean {
    return o1 && o2 ? this.getDepartmentsIdentifier(o1) === this.getDepartmentsIdentifier(o2) : o1 === o2;
  }

  addDepartmentsToCollectionIfMissing<Type extends Pick<IDepartments, 'id'>>(
    departmentsCollection: Type[],
    ...departmentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const departments: Type[] = departmentsToCheck.filter(isPresent);
    if (departments.length > 0) {
      const departmentsCollectionIdentifiers = departmentsCollection.map(
        departmentsItem => this.getDepartmentsIdentifier(departmentsItem)!,
      );
      const departmentsToAdd = departments.filter(departmentsItem => {
        const departmentsIdentifier = this.getDepartmentsIdentifier(departmentsItem);
        if (departmentsCollectionIdentifiers.includes(departmentsIdentifier)) {
          return false;
        }
        departmentsCollectionIdentifiers.push(departmentsIdentifier);
        return true;
      });
      return [...departmentsToAdd, ...departmentsCollection];
    }
    return departmentsCollection;
  }
}
