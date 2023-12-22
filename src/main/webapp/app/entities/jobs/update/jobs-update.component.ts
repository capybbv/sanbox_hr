import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IJobs } from '../jobs.model';
import { JobsService } from '../service/jobs.service';
import { JobsFormService, JobsFormGroup } from './jobs-form.service';

@Component({
  standalone: true,
  selector: 'jhi-jobs-update',
  templateUrl: './jobs-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class JobsUpdateComponent implements OnInit {
  isSaving = false;
  jobs: IJobs | null = null;

  editForm: JobsFormGroup = this.jobsFormService.createJobsFormGroup();

  constructor(
    protected jobsService: JobsService,
    protected jobsFormService: JobsFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ jobs }) => {
      this.jobs = jobs;
      if (jobs) {
        this.updateForm(jobs);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const jobs = this.jobsFormService.getJobs(this.editForm);
    if (jobs.id !== null) {
      this.subscribeToSaveResponse(this.jobsService.update(jobs));
    } else {
      this.subscribeToSaveResponse(this.jobsService.create(jobs));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJobs>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(jobs: IJobs): void {
    this.jobs = jobs;
    this.jobsFormService.resetForm(this.editForm, jobs);
  }
}
