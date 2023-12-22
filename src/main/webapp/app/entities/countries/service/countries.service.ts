import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ICountries, NewCountries } from '../countries.model';

export type PartialUpdateCountries = Partial<ICountries> & Pick<ICountries, 'id'>;

export type EntityResponseType = HttpResponse<ICountries>;
export type EntityArrayResponseType = HttpResponse<ICountries[]>;

@Injectable({ providedIn: 'root' })
export class CountriesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/countries');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/countries/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(countries: NewCountries): Observable<EntityResponseType> {
    return this.http.post<ICountries>(this.resourceUrl, countries, { observe: 'response' });
  }

  update(countries: ICountries): Observable<EntityResponseType> {
    return this.http.put<ICountries>(`${this.resourceUrl}/${this.getCountriesIdentifier(countries)}`, countries, { observe: 'response' });
  }

  partialUpdate(countries: PartialUpdateCountries): Observable<EntityResponseType> {
    return this.http.patch<ICountries>(`${this.resourceUrl}/${this.getCountriesIdentifier(countries)}`, countries, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICountries>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICountries[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICountries[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ICountries[]>()], asapScheduler)));
  }

  getCountriesIdentifier(countries: Pick<ICountries, 'id'>): number {
    return countries.id;
  }

  compareCountries(o1: Pick<ICountries, 'id'> | null, o2: Pick<ICountries, 'id'> | null): boolean {
    return o1 && o2 ? this.getCountriesIdentifier(o1) === this.getCountriesIdentifier(o2) : o1 === o2;
  }

  addCountriesToCollectionIfMissing<Type extends Pick<ICountries, 'id'>>(
    countriesCollection: Type[],
    ...countriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const countries: Type[] = countriesToCheck.filter(isPresent);
    if (countries.length > 0) {
      const countriesCollectionIdentifiers = countriesCollection.map(countriesItem => this.getCountriesIdentifier(countriesItem)!);
      const countriesToAdd = countries.filter(countriesItem => {
        const countriesIdentifier = this.getCountriesIdentifier(countriesItem);
        if (countriesCollectionIdentifiers.includes(countriesIdentifier)) {
          return false;
        }
        countriesCollectionIdentifiers.push(countriesIdentifier);
        return true;
      });
      return [...countriesToAdd, ...countriesCollection];
    }
    return countriesCollection;
  }
}
