import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ILocations, NewLocations } from '../locations.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILocations for edit and NewLocationsFormGroupInput for create.
 */
type LocationsFormGroupInput = ILocations | PartialWithRequiredKeyOf<NewLocations>;

type LocationsFormDefaults = Pick<NewLocations, 'id'>;

type LocationsFormGroupContent = {
  id: FormControl<ILocations['id'] | NewLocations['id']>;
  locationId: FormControl<ILocations['locationId']>;
  streetAddress: FormControl<ILocations['streetAddress']>;
  postalCode: FormControl<ILocations['postalCode']>;
  city: FormControl<ILocations['city']>;
  stateProvince: FormControl<ILocations['stateProvince']>;
  locCIdFk: FormControl<ILocations['locCIdFk']>;
};

export type LocationsFormGroup = FormGroup<LocationsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LocationsFormService {
  createLocationsFormGroup(locations: LocationsFormGroupInput = { id: null }): LocationsFormGroup {
    const locationsRawValue = {
      ...this.getFormDefaults(),
      ...locations,
    };
    return new FormGroup<LocationsFormGroupContent>({
      id: new FormControl(
        { value: locationsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      locationId: new FormControl(locationsRawValue.locationId, {
        validators: [Validators.required],
      }),
      streetAddress: new FormControl(locationsRawValue.streetAddress, {
        validators: [Validators.maxLength(40)],
      }),
      postalCode: new FormControl(locationsRawValue.postalCode, {
        validators: [Validators.maxLength(12)],
      }),
      city: new FormControl(locationsRawValue.city, {
        validators: [Validators.maxLength(30)],
      }),
      stateProvince: new FormControl(locationsRawValue.stateProvince, {
        validators: [Validators.maxLength(25)],
      }),
      locCIdFk: new FormControl(locationsRawValue.locCIdFk),
    });
  }

  getLocations(form: LocationsFormGroup): ILocations | NewLocations {
    return form.getRawValue() as ILocations | NewLocations;
  }

  resetForm(form: LocationsFormGroup, locations: LocationsFormGroupInput): void {
    const locationsRawValue = { ...this.getFormDefaults(), ...locations };
    form.reset(
      {
        ...locationsRawValue,
        id: { value: locationsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): LocationsFormDefaults {
    return {
      id: null,
    };
  }
}
