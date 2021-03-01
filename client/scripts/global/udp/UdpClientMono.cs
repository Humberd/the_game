using System;
using Client.scripts.global.udp.ingress;
using Godot;

namespace Client.scripts.global.udp
{
	public class UdpClientMono : Node
	{
		private readonly IngressPacketReceiver _ingressPacketReceiver = new IngressPacketReceiver();
		private readonly PacketPeerUDP _udp = new PacketPeerUDP();

		public static UdpClientMono Instance;

		public override void _Ready()
		{
			_udp.SetDestAddress("127.0.0.1", 4445);
			StartServer();
			Instance = this;
		}

		public override void _ExitTree()
		{
			Instance = null;
		}

		public override void _Process(float delta)
		{
			while (_udp.GetAvailablePacketCount() != 0)
			{
				_ingressPacketReceiver.Handle(_udp.GetPacket());
			}
		}

		private void StartServer()
		{
			var rng = new RandomNumberGenerator();
			rng.Randomize();
			var port = rng.RandiRange(999, 9999);

			if (_udp.Listen(port, "*", 512) != Error.Ok)
			{
				Console.WriteLine("Error listening on port: " + port);
			}
			else
			{
				Console.WriteLine("Listening on port: " + port);
			}
		}

		public void Send(byte[] data)
		{
			_udp.PutPacket(data);
		}

	}
}
