import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IJobs, NewJobs } from '../jobs.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IJobs for edit and NewJobsFormGroupInput for create.
 */
type JobsFormGroupInput = IJobs | PartialWithRequiredKeyOf<NewJobs>;

type JobsFormDefaults = Pick<NewJobs, 'id'>;

type JobsFormGroupContent = {
  id: FormControl<IJobs['id'] | NewJobs['id']>;
  jobId: FormControl<IJobs['jobId']>;
  jobTitle: FormControl<IJobs['jobTitle']>;
  minSalary: FormControl<IJobs['minSalary']>;
  maxSalary: FormControl<IJobs['maxSalary']>;
};

export type JobsFormGroup = FormGroup<JobsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class JobsFormService {
  createJobsFormGroup(jobs: JobsFormGroupInput = { id: null }): JobsFormGroup {
    const jobsRawValue = {
      ...this.getFormDefaults(),
      ...jobs,
    };
    return new FormGroup<JobsFormGroupContent>({
      id: new FormControl(
        { value: jobsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      jobId: new FormControl(jobsRawValue.jobId, {
        validators: [Validators.required],
      }),
      jobTitle: new FormControl(jobsRawValue.jobTitle, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      minSalary: new FormControl(jobsRawValue.minSalary),
      maxSalary: new FormControl(jobsRawValue.maxSalary),
    });
  }

  getJobs(form: JobsFormGroup): IJobs | NewJobs {
    return form.getRawValue() as IJobs | NewJobs;
  }

  resetForm(form: JobsFormGroup, jobs: JobsFormGroupInput): void {
    const jobsRawValue = { ...this.getFormDefaults(), ...jobs };
    form.reset(
      {
        ...jobsRawValue,
        id: { value: jobsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): JobsFormDefaults {
    return {
      id: null,
    };
  }
}
