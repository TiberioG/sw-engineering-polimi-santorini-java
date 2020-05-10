package it.polimi.ingsw.psp40.controller.strategies.strategyMove;

import it.polimi.ingsw.psp40.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.psp40.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.psp40.exceptions.WrongCellSelectedMoveException;
import it.polimi.ingsw.psp40.exceptions.ZeroCellsAvailableMoveException;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.List;

// ATENA

public class OthersCantLevelUp extends DefaultMove {

    /* Constructor(s) */

    public OthersCantLevelUp(Match match) {
        super(match);
    }

    /* Methods */

    @Override
    public void move(Worker worker, Cell cell) throws ZeroCellsAvailableMoveException, WrongCellSelectedMoveException, WorkerAlreadyPresentException, CellOutOfBoundsException {
        try {
            Cell origWorkerCell = match.getLocation().getLocation(worker);
            super.move(worker, cell);
            if(cell.getTower().getTopComponent().getComponentCode() > origWorkerCell.getTower().getTopComponent().getComponentCode()) { // I'm going up
                match.getMatchProperties().setOthersCantLevelUp(true);
            }
        } catch (ZeroCellsAvailableMoveException e) {
            throw new ZeroCellsAvailableMoveException();
        } catch (WrongCellSelectedMoveException e) {
            throw new WrongCellSelectedMoveException();
        } catch (WorkerAlreadyPresentException e) {
            throw new WorkerAlreadyPresentException();
        } catch (CellOutOfBoundsException e) {
            throw new CellOutOfBoundsException();
        }
    }

    @Override
    public List<Cell> getAvailableCells(Worker worker) {
        match.getMatchProperties().setOthersCantLevelUp(false);
        return super.getAvailableCells(worker);
    }
}