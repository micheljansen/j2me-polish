package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_Serializable_template
	implements Externalizable
{
	public static class InnerSerializable
		implements Externalizable
	{
		public int i;

		public void write(DataOutputStream out)
			throws IOException
		{
		}

		public void read(DataInputStream in)
			throws IOException
		{
		}
	}
	
	public InnerSerializable field;

	public void read(DataInputStream input)
		throws IOException
	{
		if (input.readBoolean())
		{
			this.field = new InnerSerializable();
			this.field.read(input);
		}
	}

	public void write(DataOutputStream output)
		throws IOException
	{
		if (this.field != null)
		{
			output.writeBoolean(true);
			this.field.write(output);
		}
		else
		{
			output.writeBoolean(false);
		}
	}
}
