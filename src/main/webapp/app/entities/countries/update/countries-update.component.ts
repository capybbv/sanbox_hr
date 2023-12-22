import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IRegion } from 'app/entities/region/region.model';
import { RegionService } from 'app/entities/region/service/region.service';
import { ICountries } from '../countries.model';
import { CountriesService } from '../service/countries.service';
import { CountriesFormService, CountriesFormGroup } from './countries-form.service';

@Component({
  standalone: true,
  selector: 'jhi-countries-update',
  templateUrl: './countries-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CountriesUpdateComponent implements OnInit {
  isSaving = false;
  countries: ICountries | null = null;

  regionsSharedCollection: IRegion[] = [];

  editForm: CountriesFormGroup = this.countriesFormService.createCountriesFormGroup();

  constructor(
    protected countriesService: CountriesService,
    protected countriesFormService: CountriesFormService,
    protected regionService: RegionService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareRegion = (o1: IRegion | null, o2: IRegion | null): boolean => this.regionService.compareRegion(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ countries }) => {
      this.countries = countries;
      if (countries) {
        this.updateForm(countries);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const countries = this.countriesFormService.getCountries(this.editForm);
    if (countries.id !== null) {
      this.subscribeToSaveResponse(this.countriesService.update(countries));
    } else {
      this.subscribeToSaveResponse(this.countriesService.create(countries));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICountries>>): void {
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

  protected updateForm(countries: ICountries): void {
    this.countries = countries;
    this.countriesFormService.resetForm(this.editForm, countries);

    this.regionsSharedCollection = this.regionService.addRegionToCollectionIfMissing<IRegion>(
      this.regionsSharedCollection,
      countries.countrRegFk,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.regionService
      .query()
      .pipe(map((res: HttpResponse<IRegion[]>) => res.body ?? []))
      .pipe(map((regions: IRegion[]) => this.regionService.addRegionToCollectionIfMissing<IRegion>(regions, this.countries?.countrRegFk)))
      .subscribe((regions: IRegion[]) => (this.regionsSharedCollection = regions));
  }
}
