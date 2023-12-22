import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ILocations, NewLocations } from '../locations.model';

export type PartialUpdateLocations = Partial<ILocations> & Pick<ILocations, 'id'>;

export type EntityResponseType = HttpResponse<ILocations>;
export type EntityArrayResponseType = HttpResponse<ILocations[]>;

@Injectable({ providedIn: 'root' })
export class LocationsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/locations');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/locations/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(locations: NewLocations): Observable<EntityResponseType> {
    return this.http.post<ILocations>(this.resourceUrl, locations, { observe: 'response' });
  }

  update(locations: ILocations): Observable<EntityResponseType> {
    return this.http.put<ILocations>(`${this.resourceUrl}/${this.getLocationsIdentifier(locations)}`, locations, { observe: 'response' });
  }

  partialUpdate(locations: PartialUpdateLocations): Observable<EntityResponseType> {
    return this.http.patch<ILocations>(`${this.resourceUrl}/${this.getLocationsIdentifier(locations)}`, locations, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILocations>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILocations[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILocations[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ILocations[]>()], asapScheduler)));
  }

  getLocationsIdentifier(locations: Pick<ILocations, 'id'>): number {
    return locations.id;
  }

  compareLocations(o1: Pick<ILocations, 'id'> | null, o2: Pick<ILocations, 'id'> | null): boolean {
    return o1 && o2 ? this.getLocationsIdentifier(o1) === this.getLocationsIdentifier(o2) : o1 === o2;
  }

  addLocationsToCollectionIfMissing<Type extends Pick<ILocations, 'id'>>(
    locationsCollection: Type[],
    ...locationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const locations: Type[] = locationsToCheck.filter(isPresent);
    if (locations.length > 0) {
      const locationsCollectionIdentifiers = locationsCollection.map(locationsItem => this.getLocationsIdentifier(locationsItem)!);
      const locationsToAdd = locations.filter(locationsItem => {
        const locationsIdentifier = this.getLocationsIdentifier(locationsItem);
        if (locationsCollectionIdentifiers.includes(locationsIdentifier)) {
          return false;
        }
        locationsCollectionIdentifiers.push(locationsIdentifier);
        return true;
      });
      return [...locationsToAdd, ...locationsCollection];
    }
    return locationsCollection;
  }
}
