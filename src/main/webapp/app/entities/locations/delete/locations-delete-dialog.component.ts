import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ILocations } from '../locations.model';
import { LocationsService } from '../service/locations.service';

@Component({
  standalone: true,
  templateUrl: './locations-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class LocationsDeleteDialogComponent {
  locations?: ILocations;

  constructor(
    protected locationsService: LocationsService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.locationsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
