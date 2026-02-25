import {SbgbStructuralChangeDialogComponent, StructuralChangeChoice} from './sbgb-structural-change-dialog.component';

describe('SbgbStructuralChangeDialogComponent', () => {
  let component: SbgbStructuralChangeDialogComponent;
  let mockDialogRef: any;

  beforeEach(() => {
    mockDialogRef = {close: jest.fn()};
    component = new SbgbStructuralChangeDialogComponent(mockDialogRef);
  });

  it('should close dialog with CLEAR choice when option A is selected', () => {
    component.selectClear();

    expect(mockDialogRef.close).toHaveBeenCalledWith(StructuralChangeChoice.CLEAR);
  });

  it('should close dialog with REAPPLY choice when option B is selected', () => {
    component.selectReapply();

    expect(mockDialogRef.close).toHaveBeenCalledWith(StructuralChangeChoice.REAPPLY);
  });

  it('should close dialog with CANCEL choice when cancelled', () => {
    component.cancel();

    expect(mockDialogRef.close).toHaveBeenCalledWith(StructuralChangeChoice.CANCEL);
  });

  it('should expose renders count via input data', () => {
    mockDialogRef = {close: jest.fn()};
    component = new SbgbStructuralChangeDialogComponent(mockDialogRef, {rendersCount: 3});

    expect(component.rendersCount).toBe(3);
  });
});
