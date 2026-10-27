import { GameScreen } from "@/components/game/GameScreen";

export default async function GamePage(props: PageProps<"/game/[id]">) {
  const { id } = await props.params;
  return <GameScreen gameId={id} />;
}
