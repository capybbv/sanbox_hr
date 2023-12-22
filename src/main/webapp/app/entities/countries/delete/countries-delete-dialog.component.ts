import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICountries } from '../countries.model';
import { CountriesService } from '../service/countries.service';

@Component({
  standalone: true,
  templateUrl: './countries-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CountriesDeleteDialogComponent {
  countries?: ICountries;

  constructor(
    protected countriesService: CountriesService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.countriesService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
