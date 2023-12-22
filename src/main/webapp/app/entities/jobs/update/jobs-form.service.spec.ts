import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../jobs.test-samples';

import { JobsFormService } from './jobs-form.service';

describe('Jobs Form Service', () => {
  let service: JobsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(JobsFormService);
  });

  describe('Service methods', () => {
    describe('createJobsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createJobsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            jobId: expect.any(Object),
            jobTitle: expect.any(Object),
            minSalary: expect.any(Object),
            maxSalary: expect.any(Object),
          }),
        );
      });

      it('passing IJobs should create a new form with FormGroup', () => {
        const formGroup = service.createJobsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            jobId: expect.any(Object),
            jobTitle: expect.any(Object),
            minSalary: expect.any(Object),
            maxSalary: expect.any(Object),
          }),
        );
      });
    });

    describe('getJobs', () => {
      it('should return NewJobs for default Jobs initial value', () => {
        const formGroup = service.createJobsFormGroup(sampleWithNewData);

        const jobs = service.getJobs(formGroup) as any;

        expect(jobs).toMatchObject(sampleWithNewData);
      });

      it('should return NewJobs for empty Jobs initial value', () => {
        const formGroup = service.createJobsFormGroup();

        const jobs = service.getJobs(formGroup) as any;

        expect(jobs).toMatchObject({});
      });

      it('should return IJobs', () => {
        const formGroup = service.createJobsFormGroup(sampleWithRequiredData);

        const jobs = service.getJobs(formGroup) as any;

        expect(jobs).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IJobs should not enable id FormControl', () => {
        const formGroup = service.createJobsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewJobs should disable id FormControl', () => {
        const formGroup = service.createJobsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
