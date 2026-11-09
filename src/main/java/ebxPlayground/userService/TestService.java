package ebxPlayground.userService;

import java.util.List;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.Request;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.ui.selection.TableViewEntitySelection;

public class TestService implements TableViewEntitySelection{

    @Override
    public AdaptationTable getTable() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTable'");
    }

    @Override
    public Adaptation getDataset() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDataset'");
    }

    @Override
    public AdaptationHome getDataspace() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDataspace'");
    }

    @Override
    public Request getAllRecords() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllRecords'");
    }

    @Override
    public List<SchemaNode> getDisplayedColumns() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDisplayedColumns'");
    }

    @Override
    public Request getFilteredRecords() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFilteredRecords'");
    }

    @Override
    public Request getSelectedRecords() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSelectedRecords'");
    }

}
