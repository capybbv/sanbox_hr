import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDepartments } from '../departments.model';
import { DepartmentsService } from '../service/departments.service';

@Component({
  standalone: true,
  templateUrl: './departments-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DepartmentsDeleteDialogComponent {
  departments?: IDepartments;

  constructor(
    protected departmentsService: DepartmentsService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.departmentsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
