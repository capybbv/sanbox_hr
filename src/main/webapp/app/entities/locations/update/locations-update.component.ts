import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICountries } from 'app/entities/countries/countries.model';
import { CountriesService } from 'app/entities/countries/service/countries.service';
import { ILocations } from '../locations.model';
import { LocationsService } from '../service/locations.service';
import { LocationsFormService, LocationsFormGroup } from './locations-form.service';

@Component({
  standalone: true,
  selector: 'jhi-locations-update',
  templateUrl: './locations-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class LocationsUpdateComponent implements OnInit {
  isSaving = false;
  locations: ILocations | null = null;

  countriesSharedCollection: ICountries[] = [];

  editForm: LocationsFormGroup = this.locationsFormService.createLocationsFormGroup();

  constructor(
    protected locationsService: LocationsService,
    protected locationsFormService: LocationsFormService,
    protected countriesService: CountriesService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareCountries = (o1: ICountries | null, o2: ICountries | null): boolean => this.countriesService.compareCountries(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ locations }) => {
      this.locations = locations;
      if (locations) {
        this.updateForm(locations);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const locations = this.locationsFormService.getLocations(this.editForm);
    if (locations.id !== null) {
      this.subscribeToSaveResponse(this.locationsService.update(locations));
    } else {
      this.subscribeToSaveResponse(this.locationsService.create(locations));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILocations>>): void {
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

  protected updateForm(locations: ILocations): void {
    this.locations = locations;
    this.locationsFormService.resetForm(this.editForm, locations);

    this.countriesSharedCollection = this.countriesService.addCountriesToCollectionIfMissing<ICountries>(
      this.countriesSharedCollection,
      locations.locCIdFk,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.countriesService
      .query()
      .pipe(map((res: HttpResponse<ICountries[]>) => res.body ?? []))
      .pipe(
        map((countries: ICountries[]) =>
          this.countriesService.addCountriesToCollectionIfMissing<ICountries>(countries, this.locations?.locCIdFk),
        ),
      )
      .subscribe((countries: ICountries[]) => (this.countriesSharedCollection = countries));
  }
}
