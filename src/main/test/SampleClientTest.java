import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SampleClientTest {
	@InjectMocks
	SampleClient sampleClient;
	
	ArrayList<String> lastNameList = null;
	
	@Before
	public void setup(){
		lastNameList = new ArrayList<>();
		lastNameList.add("Smith");
		lastNameList.add("Brown");
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void test_readLastNameFromFile(){
		sampleClient.readLastNameFromFile();
	}
	
	@Test
	public void test_makeRequest(){
		sampleClient.makeRequest();
	}

}
