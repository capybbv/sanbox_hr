import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IJobs, NewJobs } from '../jobs.model';

export type PartialUpdateJobs = Partial<IJobs> & Pick<IJobs, 'id'>;

export type EntityResponseType = HttpResponse<IJobs>;
export type EntityArrayResponseType = HttpResponse<IJobs[]>;

@Injectable({ providedIn: 'root' })
export class JobsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/jobs');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/jobs/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(jobs: NewJobs): Observable<EntityResponseType> {
    return this.http.post<IJobs>(this.resourceUrl, jobs, { observe: 'response' });
  }

  update(jobs: IJobs): Observable<EntityResponseType> {
    return this.http.put<IJobs>(`${this.resourceUrl}/${this.getJobsIdentifier(jobs)}`, jobs, { observe: 'response' });
  }

  partialUpdate(jobs: PartialUpdateJobs): Observable<EntityResponseType> {
    return this.http.patch<IJobs>(`${this.resourceUrl}/${this.getJobsIdentifier(jobs)}`, jobs, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IJobs>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IJobs[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IJobs[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IJobs[]>()], asapScheduler)));
  }

  getJobsIdentifier(jobs: Pick<IJobs, 'id'>): number {
    return jobs.id;
  }

  compareJobs(o1: Pick<IJobs, 'id'> | null, o2: Pick<IJobs, 'id'> | null): boolean {
    return o1 && o2 ? this.getJobsIdentifier(o1) === this.getJobsIdentifier(o2) : o1 === o2;
  }

  addJobsToCollectionIfMissing<Type extends Pick<IJobs, 'id'>>(
    jobsCollection: Type[],
    ...jobsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const jobs: Type[] = jobsToCheck.filter(isPresent);
    if (jobs.length > 0) {
      const jobsCollectionIdentifiers = jobsCollection.map(jobsItem => this.getJobsIdentifier(jobsItem)!);
      const jobsToAdd = jobs.filter(jobsItem => {
        const jobsIdentifier = this.getJobsIdentifier(jobsItem);
        if (jobsCollectionIdentifiers.includes(jobsIdentifier)) {
          return false;
        }
        jobsCollectionIdentifiers.push(jobsIdentifier);
        return true;
      });
      return [...jobsToAdd, ...jobsCollection];
    }
    return jobsCollection;
  }
}
