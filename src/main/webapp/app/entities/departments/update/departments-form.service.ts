import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IDepartments, NewDepartments } from '../departments.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDepartments for edit and NewDepartmentsFormGroupInput for create.
 */
type DepartmentsFormGroupInput = IDepartments | PartialWithRequiredKeyOf<NewDepartments>;

type DepartmentsFormDefaults = Pick<NewDepartments, 'id'>;

type DepartmentsFormGroupContent = {
  id: FormControl<IDepartments['id'] | NewDepartments['id']>;
  departmentId: FormControl<IDepartments['departmentId']>;
  departmentName: FormControl<IDepartments['departmentName']>;
  deptLocFk: FormControl<IDepartments['deptLocFk']>;
  depMgrFk: FormControl<IDepartments['depMgrFk']>;
};

export type DepartmentsFormGroup = FormGroup<DepartmentsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DepartmentsFormService {
  createDepartmentsFormGroup(departments: DepartmentsFormGroupInput = { id: null }): DepartmentsFormGroup {
    const departmentsRawValue = {
      ...this.getFormDefaults(),
      ...departments,
    };
    return new FormGroup<DepartmentsFormGroupContent>({
      id: new FormControl(
        { value: departmentsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      departmentId: new FormControl(departmentsRawValue.departmentId, {
        validators: [Validators.required],
      }),
      departmentName: new FormControl(departmentsRawValue.departmentName, {
        validators: [Validators.maxLength(30)],
      }),
      deptLocFk: new FormControl(departmentsRawValue.deptLocFk),
      depMgrFk: new FormControl(departmentsRawValue.depMgrFk),
    });
  }

  getDepartments(form: DepartmentsFormGroup): IDepartments | NewDepartments {
    return form.getRawValue() as IDepartments | NewDepartments;
  }

  resetForm(form: DepartmentsFormGroup, departments: DepartmentsFormGroupInput): void {
    const departmentsRawValue = { ...this.getFormDefaults(), ...departments };
    form.reset(
      {
        ...departmentsRawValue,
        id: { value: departmentsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DepartmentsFormDefaults {
    return {
      id: null,
    };
  }
}
