package ebxPlayground;

import jakarta.servlet.annotation.WebListener;

import com.orchestranetworks.module.ModuleContextOnRepositoryStartup;
import com.orchestranetworks.module.ModuleInitializedContext;
import com.orchestranetworks.module.ModuleRegistrationListener;
import com.orchestranetworks.module.ModuleServiceRegistrationContext;


@WebListener
public final class RegistrationListener extends ModuleRegistrationListener {
	
	public final void handleServiceRegistration(final ModuleServiceRegistrationContext context) {
		// register module-defined user services, service groups and user service extensions
			
	}

	@Override
    public final void handleContextInitialized(final ModuleInitializedContext aContext) {
		// handles the registration of dynamic resources and performs module-specific initializations
    }
	
	public void handleRepositoryStartup(ModuleContextOnRepositoryStartup aContext) {
		// register module-specific resources and performs module-specific initializations that require the repository.
	}
	
	public void handleRepositoryShutdown() {
		// Release resources of the current module when the repository is shut down here
	}
			 
	public void destroyBeforeUnregisterModule()
	{
		// Perform operations when this servlet is being taken out of service here
	}
}