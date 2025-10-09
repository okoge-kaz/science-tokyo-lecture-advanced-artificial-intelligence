import pandas as pd
import matplotlib.pyplot as plt

# 手法のリスト
methods = {"UNDX+MGG": "UndxMgg", "REX/JGG": "RexJgg"}

# P14固定、K5固定で比較
P = "P14"
K = "K5"

# 色とマーカーの設定
colors = {"UNDX+MGG": "blue", "REX/JGG": "red"}
markers = ["o", "s", "^"]  # 1st, 2nd, 3rd trial

plt.figure(figsize=(12, 8))

# 各手法についてプロット
for method_name, method_code in methods.items():
    filepath = f"output/report03/{method_code}KTablet{P}{K}.csv"

    try:
        df = pd.read_csv(filepath)
        x = df["NoOfEvals"]

        # 各trialをプロット（NoOfEvals以外の全カラム）
        trial_columns = [col for col in df.columns if col != "NoOfEvals"]
        for i, col in enumerate(trial_columns):
            trial_label = f"{method_name} - Trial {i + 1}"
            plt.plot(
                x,
                df[col],
                marker=markers[i],
                label=trial_label,
                color=colors[method_name],
                alpha=0.7,
                markersize=6,
                linewidth=2,
            )
    except FileNotFoundError:
        print(f"Warning: {filepath} not found")
        continue
    except Exception as e:
        print(f"Error processing {filepath}: {e}")
        continue

# y軸を対数スケールに設定
plt.yscale("log")

# グラフの装飾
plt.xlabel("Number of Evaluations", fontsize=12)
plt.ylabel("Value (Log Scale)", fontsize=12)
plt.title(f"Comparison of UNDX+MGG vs REX/JGG ({P}, {K})", fontsize=14)
plt.legend(bbox_to_anchor=(1.05, 1), loc="upper left")
plt.grid(True, which="both", ls="-", alpha=0.3)
plt.tight_layout()

# グラフの保存と表示
plt.savefig(f"figures/report03.png", dpi=300, bbox_inches="tight")
plt.show()

print("Plot has been generated successfully!")
