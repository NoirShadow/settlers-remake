package networklib.channel.listeners;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Hashtable;

import networklib.channel.Channel;
import networklib.channel.IChannelListener;
import networklib.channel.IDeserializingable;
import networklib.channel.Packet;

/**
 * This abstract class deserializes the packet received from a {@link Channel} with the {@link IDeserializingable}s supplied for each key and defines
 * a specialized method to receive the deserialized Packet in subtypes of this class.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class PacketChannelListener<T extends Packet> implements IChannelListener {

	private final int[] keys;
	private final Hashtable<Integer, IDeserializingable<T>> deserializers = new Hashtable<Integer, IDeserializingable<T>>();

	public PacketChannelListener(int key, IDeserializingable<T> deserializer) {
		this.keys = new int[] { key };
		this.deserializers.put(key, deserializer);
	}

	public PacketChannelListener(int[] keys, IDeserializingable<T>[] deserializers) {
		assert keys.length == deserializers.length;

		this.keys = keys;

		for (int i = 0; i < keys.length; i++) {
			this.deserializers.put(keys[i], deserializers[i]);
		}
	}

	@Override
	public int[] getKeys() {
		return keys;
	}

	@Override
	public final void receive(int key, DataInputStream stream) throws IOException, ClassNotFoundException {
		IDeserializingable<T> deserializer = deserializers.get(key);
		assert deserializer != null;

		receivePacket(deserializer.deserialize(key, stream));
	}

	protected abstract void receivePacket(T deserialized) throws IOException;

}
