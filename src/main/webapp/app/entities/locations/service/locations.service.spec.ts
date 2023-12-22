import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ILocations } from '../locations.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../locations.test-samples';

import { LocationsService } from './locations.service';

const requireRestSample: ILocations = {
  ...sampleWithRequiredData,
};

describe('Locations Service', () => {
  let service: LocationsService;
  let httpMock: HttpTestingController;
  let expectedResult: ILocations | ILocations[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(LocationsService);
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

    it('should create a Locations', () => {
      const locations = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(locations).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Locations', () => {
      const locations = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(locations).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Locations', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Locations', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Locations', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Locations', () => {
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

    describe('addLocationsToCollectionIfMissing', () => {
      it('should add a Locations to an empty array', () => {
        const locations: ILocations = sampleWithRequiredData;
        expectedResult = service.addLocationsToCollectionIfMissing([], locations);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(locations);
      });

      it('should not add a Locations to an array that contains it', () => {
        const locations: ILocations = sampleWithRequiredData;
        const locationsCollection: ILocations[] = [
          {
            ...locations,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLocationsToCollectionIfMissing(locationsCollection, locations);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Locations to an array that doesn't contain it", () => {
        const locations: ILocations = sampleWithRequiredData;
        const locationsCollection: ILocations[] = [sampleWithPartialData];
        expectedResult = service.addLocationsToCollectionIfMissing(locationsCollection, locations);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(locations);
      });

      it('should add only unique Locations to an array', () => {
        const locationsArray: ILocations[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const locationsCollection: ILocations[] = [sampleWithRequiredData];
        expectedResult = service.addLocationsToCollectionIfMissing(locationsCollection, ...locationsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const locations: ILocations = sampleWithRequiredData;
        const locations2: ILocations = sampleWithPartialData;
        expectedResult = service.addLocationsToCollectionIfMissing([], locations, locations2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(locations);
        expect(expectedResult).toContain(locations2);
      });

      it('should accept null and undefined values', () => {
        const locations: ILocations = sampleWithRequiredData;
        expectedResult = service.addLocationsToCollectionIfMissing([], null, locations, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(locations);
      });

      it('should return initial array if no Locations is added', () => {
        const locationsCollection: ILocations[] = [sampleWithRequiredData];
        expectedResult = service.addLocationsToCollectionIfMissing(locationsCollection, undefined, null);
        expect(expectedResult).toEqual(locationsCollection);
      });
    });

    describe('compareLocations', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLocations(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareLocations(entity1, entity2);
        const compareResult2 = service.compareLocations(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareLocations(entity1, entity2);
        const compareResult2 = service.compareLocations(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareLocations(entity1, entity2);
        const compareResult2 = service.compareLocations(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
