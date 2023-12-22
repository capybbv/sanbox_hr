import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEmployees, NewEmployees } from '../employees.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEmployees for edit and NewEmployeesFormGroupInput for create.
 */
type EmployeesFormGroupInput = IEmployees | PartialWithRequiredKeyOf<NewEmployees>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEmployees | NewEmployees> = Omit<T, 'hireDate'> & {
  hireDate?: string | null;
};

type EmployeesFormRawValue = FormValueOf<IEmployees>;

type NewEmployeesFormRawValue = FormValueOf<NewEmployees>;

type EmployeesFormDefaults = Pick<NewEmployees, 'id' | 'hireDate'>;

type EmployeesFormGroupContent = {
  id: FormControl<EmployeesFormRawValue['id'] | NewEmployees['id']>;
  employeeId: FormControl<EmployeesFormRawValue['employeeId']>;
  firstName: FormControl<EmployeesFormRawValue['firstName']>;
  lastName: FormControl<EmployeesFormRawValue['lastName']>;
  email: FormControl<EmployeesFormRawValue['email']>;
  phoneNumber: FormControl<EmployeesFormRawValue['phoneNumber']>;
  hireDate: FormControl<EmployeesFormRawValue['hireDate']>;
  salary: FormControl<EmployeesFormRawValue['salary']>;
  commissionPct: FormControl<EmployeesFormRawValue['commissionPct']>;
  empManagerFk: FormControl<EmployeesFormRawValue['empManagerFk']>;
  empDeptFk: FormControl<EmployeesFormRawValue['empDeptFk']>;
  empJobFk: FormControl<EmployeesFormRawValue['empJobFk']>;
};

export type EmployeesFormGroup = FormGroup<EmployeesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EmployeesFormService {
  createEmployeesFormGroup(employees: EmployeesFormGroupInput = { id: null }): EmployeesFormGroup {
    const employeesRawValue = this.convertEmployeesToEmployeesRawValue({
      ...this.getFormDefaults(),
      ...employees,
    });
    return new FormGroup<EmployeesFormGroupContent>({
      id: new FormControl(
        { value: employeesRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      employeeId: new FormControl(employeesRawValue.employeeId, {
        validators: [Validators.required],
      }),
      firstName: new FormControl(employeesRawValue.firstName, {
        validators: [Validators.maxLength(20)],
      }),
      lastName: new FormControl(employeesRawValue.lastName, {
        validators: [Validators.required, Validators.maxLength(25)],
      }),
      email: new FormControl(employeesRawValue.email, {
        validators: [Validators.required, Validators.maxLength(25)],
      }),
      phoneNumber: new FormControl(employeesRawValue.phoneNumber, {
        validators: [Validators.maxLength(20)],
      }),
      hireDate: new FormControl(employeesRawValue.hireDate, {
        validators: [Validators.required],
      }),
      salary: new FormControl(employeesRawValue.salary),
      commissionPct: new FormControl(employeesRawValue.commissionPct),
      empManagerFk: new FormControl(employeesRawValue.empManagerFk),
      empDeptFk: new FormControl(employeesRawValue.empDeptFk),
      empJobFk: new FormControl(employeesRawValue.empJobFk),
    });
  }

  getEmployees(form: EmployeesFormGroup): IEmployees | NewEmployees {
    return this.convertEmployeesRawValueToEmployees(form.getRawValue() as EmployeesFormRawValue | NewEmployeesFormRawValue);
  }

  resetForm(form: EmployeesFormGroup, employees: EmployeesFormGroupInput): void {
    const employeesRawValue = this.convertEmployeesToEmployeesRawValue({ ...this.getFormDefaults(), ...employees });
    form.reset(
      {
        ...employeesRawValue,
        id: { value: employeesRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): EmployeesFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      hireDate: currentTime,
    };
  }

  private convertEmployeesRawValueToEmployees(rawEmployees: EmployeesFormRawValue | NewEmployeesFormRawValue): IEmployees | NewEmployees {
    return {
      ...rawEmployees,
      hireDate: dayjs(rawEmployees.hireDate, DATE_TIME_FORMAT),
    };
  }

  private convertEmployeesToEmployeesRawValue(
    employees: IEmployees | (Partial<NewEmployees> & EmployeesFormDefaults),
  ): EmployeesFormRawValue | PartialWithRequiredKeyOf<NewEmployeesFormRawValue> {
    return {
      ...employees,
      hireDate: employees.hireDate ? employees.hireDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
