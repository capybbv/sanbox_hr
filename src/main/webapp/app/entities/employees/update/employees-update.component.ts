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
import { EmployeesService } from '../service/employees.service';
import { IEmployees } from '../employees.model';
import { EmployeesFormService, EmployeesFormGroup } from './employees-form.service';

@Component({
  standalone: true,
  selector: 'jhi-employees-update',
  templateUrl: './employees-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class EmployeesUpdateComponent implements OnInit {
  isSaving = false;
  employees: IEmployees | null = null;

  employeesSharedCollection: IEmployees[] = [];
  departmentsSharedCollection: IDepartments[] = [];
  jobsSharedCollection: IJobs[] = [];

  editForm: EmployeesFormGroup = this.employeesFormService.createEmployeesFormGroup();

  constructor(
    protected employeesService: EmployeesService,
    protected employeesFormService: EmployeesFormService,
    protected departmentsService: DepartmentsService,
    protected jobsService: JobsService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareEmployees = (o1: IEmployees | null, o2: IEmployees | null): boolean => this.employeesService.compareEmployees(o1, o2);

  compareDepartments = (o1: IDepartments | null, o2: IDepartments | null): boolean => this.departmentsService.compareDepartments(o1, o2);

  compareJobs = (o1: IJobs | null, o2: IJobs | null): boolean => this.jobsService.compareJobs(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ employees }) => {
      this.employees = employees;
      if (employees) {
        this.updateForm(employees);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const employees = this.employeesFormService.getEmployees(this.editForm);
    if (employees.id !== null) {
      this.subscribeToSaveResponse(this.employeesService.update(employees));
    } else {
      this.subscribeToSaveResponse(this.employeesService.create(employees));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEmployees>>): void {
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

  protected updateForm(employees: IEmployees): void {
    this.employees = employees;
    this.employeesFormService.resetForm(this.editForm, employees);

    this.employeesSharedCollection = this.employeesService.addEmployeesToCollectionIfMissing<IEmployees>(
      this.employeesSharedCollection,
      employees.empManagerFk,
    );
    this.departmentsSharedCollection = this.departmentsService.addDepartmentsToCollectionIfMissing<IDepartments>(
      this.departmentsSharedCollection,
      employees.empDeptFk,
    );
    this.jobsSharedCollection = this.jobsService.addJobsToCollectionIfMissing<IJobs>(this.jobsSharedCollection, employees.empJobFk);
  }

  protected loadRelationshipsOptions(): void {
    this.employeesService
      .query()
      .pipe(map((res: HttpResponse<IEmployees[]>) => res.body ?? []))
      .pipe(
        map((employees: IEmployees[]) =>
          this.employeesService.addEmployeesToCollectionIfMissing<IEmployees>(employees, this.employees?.empManagerFk),
        ),
      )
      .subscribe((employees: IEmployees[]) => (this.employeesSharedCollection = employees));

    this.departmentsService
      .query()
      .pipe(map((res: HttpResponse<IDepartments[]>) => res.body ?? []))
      .pipe(
        map((departments: IDepartments[]) =>
          this.departmentsService.addDepartmentsToCollectionIfMissing<IDepartments>(departments, this.employees?.empDeptFk),
        ),
      )
      .subscribe((departments: IDepartments[]) => (this.departmentsSharedCollection = departments));

    this.jobsService
      .query()
      .pipe(map((res: HttpResponse<IJobs[]>) => res.body ?? []))
      .pipe(map((jobs: IJobs[]) => this.jobsService.addJobsToCollectionIfMissing<IJobs>(jobs, this.employees?.empJobFk)))
      .subscribe((jobs: IJobs[]) => (this.jobsSharedCollection = jobs));
  }
}
