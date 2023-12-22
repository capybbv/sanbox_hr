import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDepartments } from 'app/entities/departments/departments.model';
import { DepartmentsService } from 'app/entities/departments/service/departments.service';
import { IJobs } from 'app/entities/jobs/jobs.model';
import { JobsService } from 'app/entities/jobs/service/jobs.service';
import { JobHistoryService } from '../service/job-history.service';
import { IJobHistory } from '../job-history.model';
import { JobHistoryFormService, JobHistoryFormGroup } from './job-history-form.service';

@Component({
  standalone: true,
  selector: 'jhi-job-history-update',
  templateUrl: './job-history-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class JobHistoryUpdateComponent implements OnInit {
  isSaving = false;
  jobHistory: IJobHistory | null = null;

  departmentsSharedCollection: IDepartments[] = [];
  jobsSharedCollection: IJobs[] = [];

  editForm: JobHistoryFormGroup = this.jobHistoryFormService.createJobHistoryFormGroup();

  constructor(
    protected jobHistoryService: JobHistoryService,
    protected jobHistoryFormService: JobHistoryFormService,
    protected departmentsService: DepartmentsService,
    protected jobsService: JobsService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareDepartments = (o1: IDepartments | null, o2: IDepartments | null): boolean => this.departmentsService.compareDepartments(o1, o2);

  compareJobs = (o1: IJobs | null, o2: IJobs | null): boolean => this.jobsService.compareJobs(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ jobHistory }) => {
      this.jobHistory = jobHistory;
      if (jobHistory) {
        this.updateForm(jobHistory);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const jobHistory = this.jobHistoryFormService.getJobHistory(this.editForm);
    if (jobHistory.id !== null) {
      this.subscribeToSaveResponse(this.jobHistoryService.update(jobHistory));
    } else {
      this.subscribeToSaveResponse(this.jobHistoryService.create(jobHistory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJobHistory>>): void {
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

  protected updateForm(jobHistory: IJobHistory): void {
    this.jobHistory = jobHistory;
    this.jobHistoryFormService.resetForm(this.editForm, jobHistory);

    this.departmentsSharedCollection = this.departmentsService.addDepartmentsToCollectionIfMissing<IDepartments>(
      this.departmentsSharedCollection,
      jobHistory.jhistDepFk,
    );
    this.jobsSharedCollection = this.jobsService.addJobsToCollectionIfMissing<IJobs>(this.jobsSharedCollection, jobHistory.jhistJob);
  }

  protected loadRelationshipsOptions(): void {
    this.departmentsService
      .query()
      .pipe(map((res: HttpResponse<IDepartments[]>) => res.body ?? []))
      .pipe(
        map((departments: IDepartments[]) =>
          this.departmentsService.addDepartmentsToCollectionIfMissing<IDepartments>(departments, this.jobHistory?.jhistDepFk),
        ),
      )
      .subscribe((departments: IDepartments[]) => (this.departmentsSharedCollection = departments));

    this.jobsService
      .query()
      .pipe(map((res: HttpResponse<IJobs[]>) => res.body ?? []))
      .pipe(map((jobs: IJobs[]) => this.jobsService.addJobsToCollectionIfMissing<IJobs>(jobs, this.jobHistory?.jhistJob)))
      .subscribe((jobs: IJobs[]) => (this.jobsSharedCollection = jobs));
  }
}
