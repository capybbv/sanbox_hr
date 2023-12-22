import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../departments.test-samples';

import { DepartmentsFormService } from './departments-form.service';

describe('Departments Form Service', () => {
  let service: DepartmentsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DepartmentsFormService);
  });

  describe('Service methods', () => {
    describe('createDepartmentsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDepartmentsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            departmentId: expect.any(Object),
            departmentName: expect.any(Object),
            deptLocFk: expect.any(Object),
            depMgrFk: expect.any(Object),
          }),
        );
      });

      it('passing IDepartments should create a new form with FormGroup', () => {
        const formGroup = service.createDepartmentsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            departmentId: expect.any(Object),
            departmentName: expect.any(Object),
            deptLocFk: expect.any(Object),
            depMgrFk: expect.any(Object),
          }),
        );
      });
    });

    describe('getDepartments', () => {
      it('should return NewDepartments for default Departments initial value', () => {
        const formGroup = service.createDepartmentsFormGroup(sampleWithNewData);

        const departments = service.getDepartments(formGroup) as any;

        expect(departments).toMatchObject(sampleWithNewData);
      });

      it('should return NewDepartments for empty Departments initial value', () => {
        const formGroup = service.createDepartmentsFormGroup();

        const departments = service.getDepartments(formGroup) as any;

        expect(departments).toMatchObject({});
      });

      it('should return IDepartments', () => {
        const formGroup = service.createDepartmentsFormGroup(sampleWithRequiredData);

        const departments = service.getDepartments(formGroup) as any;

        expect(departments).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDepartments should not enable id FormControl', () => {
        const formGroup = service.createDepartmentsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDepartments should disable id FormControl', () => {
        const formGroup = service.createDepartmentsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
