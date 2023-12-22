import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IEmployees } from 'app/entities/employees/employees.model';
import { EmployeesService } from 'app/entities/employees/service/employees.service';
import { ILocations } from 'app/entities/locations/locations.model';
import { LocationsService } from 'app/entities/locations/service/locations.service';
import { DepartmentsService } from '../service/departments.service';
import { IDepartments } from '../departments.model';
import { DepartmentsFormService, DepartmentsFormGroup } from './departments-form.service';

@Component({
  standalone: true,
  selector: 'jhi-departments-update',
  templateUrl: './departments-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DepartmentsUpdateComponent implements OnInit {
  isSaving = false;
  departments: IDepartments | null = null;

  employeesSharedCollection: IEmployees[] = [];
  locationsSharedCollection: ILocations[] = [];

  editForm: DepartmentsFormGroup = this.departmentsFormService.createDepartmentsFormGroup();

  constructor(
    protected departmentsService: DepartmentsService,
    protected departmentsFormService: DepartmentsFormService,
    protected employeesService: EmployeesService,
    protected locationsService: LocationsService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareEmployees = (o1: IEmployees | null, o2: IEmployees | null): boolean => this.employeesService.compareEmployees(o1, o2);

  compareLocations = (o1: ILocations | null, o2: ILocations | null): boolean => this.locationsService.compareLocations(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ departments }) => {
      this.departments = departments;
      if (departments) {
        this.updateForm(departments);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const departments = this.departmentsFormService.getDepartments(this.editForm);
    if (departments.id !== null) {
      this.subscribeToSaveResponse(this.departmentsService.update(departments));
    } else {
      this.subscribeToSaveResponse(this.departmentsService.create(departments));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDepartments>>): void {
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

  protected updateForm(departments: IDepartments): void {
    this.departments = departments;
    this.departmentsFormService.resetForm(this.editForm, departments);

    this.employeesSharedCollection = this.employeesService.addEmployeesToCollectionIfMissing<IEmployees>(
      this.employeesSharedCollection,
      departments.depMgrFk,
    );
    this.locationsSharedCollection = this.locationsService.addLocationsToCollectionIfMissing<ILocations>(
      this.locationsSharedCollection,
      departments.deptLocFk,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.employeesService
      .query()
      .pipe(map((res: HttpResponse<IEmployees[]>) => res.body ?? []))
      .pipe(
        map((employees: IEmployees[]) =>
          this.employeesService.addEmployeesToCollectionIfMissing<IEmployees>(employees, this.departments?.depMgrFk),
        ),
      )
      .subscribe((employees: IEmployees[]) => (this.employeesSharedCollection = employees));

    this.locationsService
      .query()
      .pipe(map((res: HttpResponse<ILocations[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocations[]) =>
          this.locationsService.addLocationsToCollectionIfMissing<ILocations>(locations, this.departments?.deptLocFk),
        ),
      )
      .subscribe((locations: ILocations[]) => (this.locationsSharedCollection = locations));
  }
}
