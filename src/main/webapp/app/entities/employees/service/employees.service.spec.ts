import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEmployees } from '../employees.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../employees.test-samples';

import { EmployeesService, RestEmployees } from './employees.service';

const requireRestSample: RestEmployees = {
  ...sampleWithRequiredData,
  hireDate: sampleWithRequiredData.hireDate?.toJSON(),
};

describe('Employees Service', () => {
  let service: EmployeesService;
  let httpMock: HttpTestingController;
  let expectedResult: IEmployees | IEmployees[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EmployeesService);
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

    it('should create a Employees', () => {
      const employees = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(employees).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Employees', () => {
      const employees = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(employees).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Employees', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Employees', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Employees', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Employees', () => {
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

    describe('addEmployeesToCollectionIfMissing', () => {
      it('should add a Employees to an empty array', () => {
        const employees: IEmployees = sampleWithRequiredData;
        expectedResult = service.addEmployeesToCollectionIfMissing([], employees);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(employees);
      });

      it('should not add a Employees to an array that contains it', () => {
        const employees: IEmployees = sampleWithRequiredData;
        const employeesCollection: IEmployees[] = [
          {
            ...employees,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEmployeesToCollectionIfMissing(employeesCollection, employees);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Employees to an array that doesn't contain it", () => {
        const employees: IEmployees = sampleWithRequiredData;
        const employeesCollection: IEmployees[] = [sampleWithPartialData];
        expectedResult = service.addEmployeesToCollectionIfMissing(employeesCollection, employees);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(employees);
      });

      it('should add only unique Employees to an array', () => {
        const employeesArray: IEmployees[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const employeesCollection: IEmployees[] = [sampleWithRequiredData];
        expectedResult = service.addEmployeesToCollectionIfMissing(employeesCollection, ...employeesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const employees: IEmployees = sampleWithRequiredData;
        const employees2: IEmployees = sampleWithPartialData;
        expectedResult = service.addEmployeesToCollectionIfMissing([], employees, employees2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(employees);
        expect(expectedResult).toContain(employees2);
      });

      it('should accept null and undefined values', () => {
        const employees: IEmployees = sampleWithRequiredData;
        expectedResult = service.addEmployeesToCollectionIfMissing([], null, employees, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(employees);
      });

      it('should return initial array if no Employees is added', () => {
        const employeesCollection: IEmployees[] = [sampleWithRequiredData];
        expectedResult = service.addEmployeesToCollectionIfMissing(employeesCollection, undefined, null);
        expect(expectedResult).toEqual(employeesCollection);
      });
    });

    describe('compareEmployees', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEmployees(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEmployees(entity1, entity2);
        const compareResult2 = service.compareEmployees(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEmployees(entity1, entity2);
        const compareResult2 = service.compareEmployees(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEmployees(entity1, entity2);
        const compareResult2 = service.compareEmployees(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
