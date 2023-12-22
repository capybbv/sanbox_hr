import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IJobs } from '../jobs.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../jobs.test-samples';

import { JobsService } from './jobs.service';

const requireRestSample: IJobs = {
  ...sampleWithRequiredData,
};

describe('Jobs Service', () => {
  let service: JobsService;
  let httpMock: HttpTestingController;
  let expectedResult: IJobs | IJobs[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(JobsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Jobs', () => {
      const jobs = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(jobs).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Jobs', () => {
      const jobs = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(jobs).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Jobs', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Jobs', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Jobs', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Jobs', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addJobsToCollectionIfMissing', () => {
      it('should add a Jobs to an empty array', () => {
        const jobs: IJobs = sampleWithRequiredData;
        expectedResult = service.addJobsToCollectionIfMissing([], jobs);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(jobs);
      });

      it('should not add a Jobs to an array that contains it', () => {
        const jobs: IJobs = sampleWithRequiredData;
        const jobsCollection: IJobs[] = [
          {
            ...jobs,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addJobsToCollectionIfMissing(jobsCollection, jobs);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Jobs to an array that doesn't contain it", () => {
        const jobs: IJobs = sampleWithRequiredData;
        const jobsCollection: IJobs[] = [sampleWithPartialData];
        expectedResult = service.addJobsToCollectionIfMissing(jobsCollection, jobs);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(jobs);
      });

      it('should add only unique Jobs to an array', () => {
        const jobsArray: IJobs[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const jobsCollection: IJobs[] = [sampleWithRequiredData];
        expectedResult = service.addJobsToCollectionIfMissing(jobsCollection, ...jobsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const jobs: IJobs = sampleWithRequiredData;
        const jobs2: IJobs = sampleWithPartialData;
        expectedResult = service.addJobsToCollectionIfMissing([], jobs, jobs2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(jobs);
        expect(expectedResult).toContain(jobs2);
      });

      it('should accept null and undefined values', () => {
        const jobs: IJobs = sampleWithRequiredData;
        expectedResult = service.addJobsToCollectionIfMissing([], null, jobs, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(jobs);
      });

      it('should return initial array if no Jobs is added', () => {
        const jobsCollection: IJobs[] = [sampleWithRequiredData];
        expectedResult = service.addJobsToCollectionIfMissing(jobsCollection, undefined, null);
        expect(expectedResult).toEqual(jobsCollection);
      });
    });

    describe('compareJobs', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareJobs(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareJobs(entity1, entity2);
        const compareResult2 = service.compareJobs(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareJobs(entity1, entity2);
        const compareResult2 = service.compareJobs(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareJobs(entity1, entity2);
        const compareResult2 = service.compareJobs(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
