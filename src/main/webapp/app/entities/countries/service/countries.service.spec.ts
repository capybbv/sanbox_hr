import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICountries } from '../countries.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../countries.test-samples';

import { CountriesService } from './countries.service';

const requireRestSample: ICountries = {
  ...sampleWithRequiredData,
};

describe('Countries Service', () => {
  let service: CountriesService;
  let httpMock: HttpTestingController;
  let expectedResult: ICountries | ICountries[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CountriesService);
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

    it('should create a Countries', () => {
      const countries = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(countries).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Countries', () => {
      const countries = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(countries).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Countries', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Countries', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Countries', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Countries', () => {
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

    describe('addCountriesToCollectionIfMissing', () => {
      it('should add a Countries to an empty array', () => {
        const countries: ICountries = sampleWithRequiredData;
        expectedResult = service.addCountriesToCollectionIfMissing([], countries);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(countries);
      });

      it('should not add a Countries to an array that contains it', () => {
        const countries: ICountries = sampleWithRequiredData;
        const countriesCollection: ICountries[] = [
          {
            ...countries,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCountriesToCollectionIfMissing(countriesCollection, countries);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Countries to an array that doesn't contain it", () => {
        const countries: ICountries = sampleWithRequiredData;
        const countriesCollection: ICountries[] = [sampleWithPartialData];
        expectedResult = service.addCountriesToCollectionIfMissing(countriesCollection, countries);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(countries);
      });

      it('should add only unique Countries to an array', () => {
        const countriesArray: ICountries[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const countriesCollection: ICountries[] = [sampleWithRequiredData];
        expectedResult = service.addCountriesToCollectionIfMissing(countriesCollection, ...countriesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const countries: ICountries = sampleWithRequiredData;
        const countries2: ICountries = sampleWithPartialData;
        expectedResult = service.addCountriesToCollectionIfMissing([], countries, countries2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(countries);
        expect(expectedResult).toContain(countries2);
      });

      it('should accept null and undefined values', () => {
        const countries: ICountries = sampleWithRequiredData;
        expectedResult = service.addCountriesToCollectionIfMissing([], null, countries, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(countries);
      });

      it('should return initial array if no Countries is added', () => {
        const countriesCollection: ICountries[] = [sampleWithRequiredData];
        expectedResult = service.addCountriesToCollectionIfMissing(countriesCollection, undefined, null);
        expect(expectedResult).toEqual(countriesCollection);
      });
    });

    describe('compareCountries', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCountries(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCountries(entity1, entity2);
        const compareResult2 = service.compareCountries(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCountries(entity1, entity2);
        const compareResult2 = service.compareCountries(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCountries(entity1, entity2);
        const compareResult2 = service.compareCountries(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
