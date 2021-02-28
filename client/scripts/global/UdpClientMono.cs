using System;
using Godot;

namespace Client.scripts.global
{
	public class UdpClientMono : Node
	{
		private readonly PacketPeerUDP _udp = new PacketPeerUDP();

		public static UdpClientMono Instance;

		public override void _Ready()
		{
			_udp.SetDestAddress("127.0.0.1", 4445);
			StartServer();
			Instance = this;
		}

		public override void _Process(float delta)
		{
			if (_udp.GetAvailablePacketCount() > 0)
			{
				Console.WriteLine(_udp.GetPacket());
			}
		}

		private void StartServer()
		{
			var rng = new RandomNumberGenerator();
			rng.Randomize();
			var port = rng.RandiRange(999, 9999);

			if (_udp.Listen(port, "*", 256) != Error.Ok)
			{
				Console.WriteLine("Error listening on port: " + port);
			}
			else
			{
				Console.WriteLine("Listening on port: " + port);
			}
		}

		public void send(string data)
		{
			_udp.PutPacket(data.ToAscii());
		}

		public void send(byte[] data)
		{
			Console.WriteLine(data);
			_udp.PutPacket(data);
		}

	}
}
