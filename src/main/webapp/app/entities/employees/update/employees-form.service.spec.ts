import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../employees.test-samples';

import { EmployeesFormService } from './employees-form.service';

describe('Employees Form Service', () => {
  let service: EmployeesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EmployeesFormService);
  });

  describe('Service methods', () => {
    describe('createEmployeesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEmployeesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            employeeId: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            email: expect.any(Object),
            phoneNumber: expect.any(Object),
            hireDate: expect.any(Object),
            salary: expect.any(Object),
            commissionPct: expect.any(Object),
            empManagerFk: expect.any(Object),
            empDeptFk: expect.any(Object),
            empJobFk: expect.any(Object),
          }),
        );
      });

      it('passing IEmployees should create a new form with FormGroup', () => {
        const formGroup = service.createEmployeesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            employeeId: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            email: expect.any(Object),
            phoneNumber: expect.any(Object),
            hireDate: expect.any(Object),
            salary: expect.any(Object),
            commissionPct: expect.any(Object),
            empManagerFk: expect.any(Object),
            empDeptFk: expect.any(Object),
            empJobFk: expect.any(Object),
          }),
        );
      });
    });

    describe('getEmployees', () => {
      it('should return NewEmployees for default Employees initial value', () => {
        const formGroup = service.createEmployeesFormGroup(sampleWithNewData);

        const employees = service.getEmployees(formGroup) as any;

        expect(employees).toMatchObject(sampleWithNewData);
      });

      it('should return NewEmployees for empty Employees initial value', () => {
        const formGroup = service.createEmployeesFormGroup();

        const employees = service.getEmployees(formGroup) as any;

        expect(employees).toMatchObject({});
      });

      it('should return IEmployees', () => {
        const formGroup = service.createEmployeesFormGroup(sampleWithRequiredData);

        const employees = service.getEmployees(formGroup) as any;

        expect(employees).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEmployees should not enable id FormControl', () => {
        const formGroup = service.createEmployeesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEmployees should disable id FormControl', () => {
        const formGroup = service.createEmployeesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
